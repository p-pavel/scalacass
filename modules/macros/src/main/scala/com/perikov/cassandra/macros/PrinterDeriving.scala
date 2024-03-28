package com.perikov.cassandra.macros

import scala.quoted.*
import scala.annotation.experimental

@experimental
transparent inline def derivePrinting[T]: T = ${ derivePrintingImpl[T] }

@experimental
def derivePrintingImpl[T: Type](using Quotes) =
  import quotes.reflect.*

  val traitTypeRepr = TypeRepr.of[T].dealias
  val traitTypeTree = TypeTree.of[T]
  val traitMethods  = traitTypeRepr.typeSymbol.methodMembers
  val parents       = List(TypeTree.of[Object], traitTypeTree)

  def decls(cls: Symbol) =
    traitMethods.map(s => traitTypeRepr.select(s).termSymbol.tree).collect {
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
              val memberType = traitTypeRepr.memberType(tpt.symbol)
              // report.info(s"Param $name: memberType ${tpt.tpe.show(using Printer.TypeReprStructure)}")
              tpt.tpe.dealias //TODO: do i need changeOwner?
            },
          _ => 
            report.info(s"res type tree: ${resType.show(using Printer.TreeStructure)}")
            val effectiveResType = resType
            val dealiasedResType = resType.tpe.dealias
            val memberType = traitTypeRepr.memberType(effectiveResType.symbol)
            val selectType = traitTypeRepr.select(effectiveResType.symbol)
            val dealiasedSelectType = selectType.dealiasKeepOpaques
            report.info(s"""
            | trait: ${traitTypeRepr.show(using Printer.TypeReprAnsiCode)}
            | effectiveResType: ${effectiveResType.show(using Printer.TreeAnsiCode)}
            | dealiasedResType: ${dealiasedResType.show(using Printer.TypeReprAnsiCode)}
            | selectType: ${selectType.show(using Printer.TypeReprAnsiCode)}
            | dealiasedSelectType: ${dealiasedSelectType.show(using Printer.TypeReprAnsiCode)}
            | memberType: ${memberType.show(using Printer.TypeReprAnsiCode)}
            |""".stripMargin)
            
            val tt = traitTypeRepr.select(d.symbol)
            val isMethod = tt.termSymbol.isDefDef
            // report.info(s"isMethod: $isMethod, effectiveResType: ${effectiveResType.show(using Printer.TypeReprAnsiCode)}")

            dealiasedSelectType
            // effectiveResType.tpe.dealias
            // dealiasedResType
        )
        Symbol.newMethod(cls, name, mtype, Flags.Override, Symbol.noSymbol)
    }

  val classSymbol = Symbol.newClass(
    Symbol.spliceOwner,
    "Printer",
    parents.map(_.tpe),
    decls,
    None
  )
  def definitions = classSymbol.declaredMethods.map(sym =>
    DefDef(
      sym,
      args => {
        val methName = Expr(sym.name)
        val argNames = Expr.ofList(args.head.map(_.asExpr))
        Some('{ s"${$methName}(${$argNames.mkString(", ")})" }.asTerm)
      }
    )
  )

  val classDef = ClassDef(classSymbol, parents, definitions)
  report.info(classDef.show(using Printer.TreeAnsiCode))

  val newCls = Typed(
    Apply(
      Select(New(TypeIdent(classSymbol)), classSymbol.primaryConstructor),
      Nil
    ),
    traitTypeTree
  )

  val block = Block(List(classDef), newCls)

  block.asExprOf[T]
