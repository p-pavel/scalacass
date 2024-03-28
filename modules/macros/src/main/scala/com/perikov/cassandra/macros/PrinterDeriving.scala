package com.perikov.cassandra.macros

import scala.quoted.*
import scala.annotation.experimental

@experimental
transparent inline def derivePrinting[T]: T = ${ derivePrintingImpl[T] }

@experimental
def derivePrintingImpl[T: Type](using Quotes) =
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
        val mtype  = MethodType(params.map(_.name))(
          m =>
            params.collect { case ValDef(name, tpt, _) =>
              val sym = tpt.symbol
              val memType = traitTypeRepr.select(sym)
              println(s"======== ${sym.isAbstractType}, ValDef $name: ${memType.show(using Printer.TypeReprStructure)}")
              if sym.isAbstractType then memType else tpt.tpe
              // memType
            },
          _ =>
            report.info(
              s"res type t: ${resType.tpe.show(using Printer.TypeReprStructure)}"
            )
            val effectiveResType    = resType
            val dealiasedResType    = resType.tpe.dealias
            val memberType          = traitTypeRepr.memberType(effectiveResType.symbol)
            val selectType          = traitTypeRepr.select(effectiveResType.symbol)
            val dealiasedSelectType = selectType.dealiasKeepOpaques
            report.info(s"""
            | trait: ${traitTypeRepr.show(using Printer.TypeReprAnsiCode)}
            | effectiveResType: ${effectiveResType.show(using
                            Printer.TreeAnsiCode
                          )}
            | dealiasedResType: ${dealiasedResType.show(using
                            Printer.TypeReprAnsiCode
                          )}
            | selectType: ${selectType.show(using Printer.TypeReprAnsiCode)}
            | dealiasedSelectType: ${dealiasedSelectType.show(using
                            Printer.TypeReprAnsiCode
                          )}
            | memberType: ${memberType.show(using Printer.TypeReprAnsiCode)}
            |""".stripMargin)

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
        val argNames = Expr.ofList(args.head.map(_.asExpr))
        Some('{ s"${$methName}(${$argNames.mkString(", ")})" }.asTerm)
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
