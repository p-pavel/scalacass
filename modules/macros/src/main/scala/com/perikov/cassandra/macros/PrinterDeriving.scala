package com.perikov.cassandra.macros

import scala.quoted.*
import scala.annotation.experimental

/** Tries to derive a printer implementation for trait T Can be used for
  * tagless-final style traits.
  * {{{
  * trait Abc[T]:
  *   def f1(g: Int, a: String): T
  *
  * derivePrinting[Abc[String]]
  * }}}
  * Will produce an instance of class inherited from `Abc[String]` with `f1`
  * implemented as
  * {{{
  * class Printer extends Abc[String]:
  *   override def f1(g: Int, a: String): String = s"f1($g, $a)"
  * }}}
  *
  * abstract type members are also supported
  *
  * @note
  *   the type parameter `T` should be a class type and not a refined type. Type
  *   aliases are followed though
  *
  * @tparam T
  *   the trait to derive a printer for
  * @return
  *   The implementation of `T`
  */
@experimental
transparent inline def derivePrinting[T]: T = ${ derivePrintingImpl[T](false) }

@experimental
transparent inline def debugDerivePrinting[T]: T = ${
  derivePrintingImpl[T](true)
}

@experimental
def derivePrintingImpl[T: Type](debug: Boolean)(using Quotes) =
  import quotes.reflect.*

  val traitTypeRepr       = TypeRepr.of[T].dealias
  val traitTypeTree       = TypeTree.of[T]
  val allTraitMethods     = traitTypeRepr.typeSymbol.methodMembers
  val printerClassParents = List(TypeTree.of[Object], traitTypeTree)

  def methodsToOverride(cls: Symbol) =
    allTraitMethods.map(s => traitTypeRepr.select(s).termSymbol.tree).collect {
      case d @ DefDef(name, paramClauses, resType, None)
          if d.symbol.flags.is(Flags.Deferred) =>
        if paramClauses.size > 1 then
          report.errorAndAbort(
            s"Only one parameter list is supported for method $name. Got ${paramClauses.size}"
          )
        val params = paramClauses.map(_.params).flatten
        val mtype  =
          if paramClauses.isEmpty
          then ByNameType(TypeRepr.of[String])
          else
            MethodType(params.map(_.name))(
              m =>
                params.collect { case ValDef(name, tpt, _) =>
                  val sym = tpt.symbol
                  if sym.isAbstractType then traitTypeRepr.select(sym)
                  else tpt.tpe
                }
              ,
              _ =>
                val selectType          = traitTypeRepr.select(resType.symbol)
                val dealiasedSelectType = selectType.dealiasKeepOpaques
                dealiasedSelectType
            )
        Symbol.newMethod(cls, name, mtype, Flags.Override, Symbol.noSymbol)
    }

  val printerClassSymbol         = Symbol.newClass(
    Symbol.spliceOwner,
    "Printer",
    printerClassParents.map(_.tpe),
    methodsToOverride,
    None
  )
  def overridenMethodDefinitions = printerClassSymbol.declaredMethods.map(sym =>
    DefDef(
      sym,
      args => {
        val methName = Expr(sym.name)
        val expr = 
          if args.isEmpty then
            '{ s"${$methName}" }
          else 
            val argNames = Expr.ofList(args.head.map(_.asExpr))
            '{ s"${$methName}(${$argNames.mkString(", ")})" }
        Some(expr.asTerm)
      }
    )
  )

  val printerClassDef = ClassDef(
    printerClassSymbol,
    printerClassParents,
    overridenMethodDefinitions
  )

  val classInstantiation = Typed(
    Apply(
      Select(
        New(TypeIdent(printerClassSymbol)),
        printerClassSymbol.primaryConstructor
      ),
      Nil
    ),
    traitTypeTree
  )

  val block = Block(List(printerClassDef), classInstantiation)

  block.asExprOf[T]
end derivePrintingImpl
