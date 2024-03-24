package com.perikov.cassandra.macros

import scala.quoted.*
import scala.annotation.experimental

@experimental
transparent inline def derivePrinting[T]: T = ${ derivePrintingImpl[T] }

@experimental
def derivePrintingImpl[T: Type](using Quotes) =
  import quotes.reflect.*

  val traitTypeRepr = TypeRepr.of[T]
  val traitTypeTree = TypeTree.of[T]
  val traitMethods  = traitTypeRepr.typeSymbol.declaredMethods
  val parents       = List(TypeTree.of[Object], traitTypeTree)

  def decls(cls: Symbol) =
    traitMethods.map(_.tree).collect {
      case d @ DefDef(name, paramClauses, resType, None) =>
        if paramClauses.size > 1 then
          report.errorAndAbort(
            s"Only one parameter list is supported for method $name"
          )
        val params = paramClauses.head.params
        val mtype  = MethodType(params.map(_.name))(
          _ => params.collect { case ValDef(name, tpt, _) => tpt.tpe },
          _ => TypeRepr.of[String] // resType.tpe
        )
        Symbol.newMethod(cls, name, mtype)
    }

  val classSymbol = Symbol.newClass(
    Symbol.spliceOwner,
    "Printer",
    parents.map(_.tpe),
    decls,
    None
  )
  def definitions = classSymbol.declaredMethods.map(sym =>
    DefDef(sym, args => {
      val methName = Expr(sym.name)
      val t = Expr.ofList( args.head.map(_.asExpr))
      Some('{ s"${$methName}(${$t.mkString(", ")})" }.asTerm)
    })
  )

  val classDef = ClassDef(classSymbol, parents, definitions)

  val newCls = Typed(
    Apply(
      Select(New(TypeIdent(classSymbol)), classSymbol.primaryConstructor),
      Nil
    ),
    traitTypeTree
  )

  val block = Block(List(classDef), newCls)

  block.asExprOf[T]
