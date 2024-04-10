package com.perikov.cassandra.macros

import scala.quoted.*
import scala.annotation.experimental

@experimental
class DerivingUtils[T: Type](derivedClassName: String)(using Quotes):
  import quotes.reflect.*
  private lazy val typeRepr = TypeRepr.of[T].dealias
  private lazy val typeTree = TypeTree.of[T]

  private def implementationParents: List[TypeTree] =
    List(TypeTree.of[Object], typeTree)

  private def candidatesForOverride =
    typeRepr.typeSymbol.methodMembers
      .map(s => typeRepr.select(s).termSymbol.tree)
      .collect {
        case d: DefDef if d.symbol.flags.is(Flags.Deferred) =>
          if d.paramss.size > 1 then
            report.errorAndAbort(
              s"Only one parameter list is supported for method ${d.name}. Got ${d.paramss.size}"
            )
          d
      }

  private def generateClassSymbol: Symbol =
    Symbol.newClass(
      Symbol.spliceOwner,
      derivedClassName,
      implementationParents.map(_.tpe),
      cls => candidatesForOverride.map(overridenMethoSymbol(cls)),
      None
    )

  private def overridenMethoSymbol(cls: Symbol)(
      methodToOverride: DefDef
  ): Symbol =
    val mtype =
      if methodToOverride.paramss.isEmpty then ByNameType(TypeRepr.of[String])
      else
        val params = methodToOverride.paramss.map(_.params).flatten
        MethodType(params.map(_.name))(
          m =>
            params.collect { case ValDef(name, tpt, _) =>
              val sym = tpt.symbol
              if sym.isAbstractType then typeRepr.select(sym)
              else tpt.tpe
            },
          _ =>
            typeRepr
              .select(methodToOverride.returnTpt.symbol)
              .dealiasKeepOpaques
        )
    Symbol.newMethod(
      cls,
      methodToOverride.name,
      mtype,
      Flags.Override,
      Symbol.noSymbol
    )

  private def overridenMethodDefinitions =
    generateClassSymbol.declaredMethods.map(sym =>
      DefDef(
        sym,
        args => {
          val methName = Expr(sym.name)
          val expr     =
            if args.isEmpty then '{ s"${$methName}" }
            else
              val argNames = Expr.ofList(args.head.map(_.asExpr))
              '{ s"${$methName}(${$argNames.mkString(", ")})" }
          Some(expr.asTerm)
        }
      )
    )

  def generatePrinterImplementation: Expr[T] = generateClassInstantiation(
    overridenMethodDefinitions
  )

  private def generateClassInstantiation(methodDefs: List[DefDef]): Expr[T] =
    val classSymbol     = generateClassSymbol
    val printerClassDef =
      ClassDef(classSymbol, implementationParents, methodDefs)

    val classInstantiation = Typed(
      Apply(
        Select(
          New(TypeIdent(classSymbol)),
          classSymbol.primaryConstructor
        ),
        Nil
      ),
      TypeTree.of[T]
    )

    val block = Block(List(printerClassDef), classInstantiation)

    block.asExprOf[T]
  end generateClassInstantiation
end DerivingUtils
