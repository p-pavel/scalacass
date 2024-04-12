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

  private lazy val generatedClassSymbol: Symbol =
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
    def unabstractTypeTree(tpt: TypeTree) =
      val sym = tpt.symbol
      if sym.isAbstractType then typeRepr.select(sym)
      else tpt.tpe

    val ret   = unabstractTypeTree(methodToOverride.returnTpt).dealiasKeepOpaques
    val mtype =
      if methodToOverride.paramss.isEmpty then ByNameType(ret)
      else
        val params = methodToOverride.paramss.map(_.params).flatten
        MethodType(params.map(_.name))(
          m =>
            params.collect { case ValDef(name, tpt, _) =>
              unabstractTypeTree(tpt)
            },
          _ => ret
        )
    Symbol.newMethod(
      cls,
      methodToOverride.name,
      mtype,
      Flags.Override,
      Symbol.noSymbol
    )

  private def overridenPrinterMethod(sym: Symbol): DefDef =
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

  def generatePrinterImplementation: Expr[T] = generateClassInstantiation(
    overridenPrinterMethod
  )

  def generateSerializerImplementation: Expr[T] = generateClassInstantiation(
    overridenPrinterMethod
  )

  private def generateClassInstantiation(
      methodDefs: Symbol => DefDef
  ): Expr[T] =
    val classSymbol     = generatedClassSymbol
    val printerClassDef =
      ClassDef(
        classSymbol,
        implementationParents,
        generatedClassSymbol.declaredMethods.map(methodDefs)
      )

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
