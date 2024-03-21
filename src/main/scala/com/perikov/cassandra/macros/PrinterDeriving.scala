package com.perikov.cassandra.macros

import scala.quoted.*
import scala.annotation.experimental

@experimental
transparent inline def derivePrinting[T]: T = ${ derivePrintingImpl[T] }

/** @see
  *   https://stackoverflow.com/questions/68550985/method-override-with-scala-3-macros
  */
@experimental
def derivePrintingImpl[T: Type](using Quotes) =
  import quotes.reflect.*

  val parents = List(TypeTree.of[Object], TypeTree.of[T])

  def decls(cls: Symbol) = List(
    Symbol.newMethod(
      cls,
      "f1",
      MethodType(List("g", "a"))(
        _ => List(TypeRepr.of[Int], TypeRepr.of[String]),
        _ => TypeRepr.of[String]
      )
    )
  )

  val classSymbol = Symbol.newClass(
    Symbol.spliceOwner,
    "Printer",
    parents.map(_.tpe),
    decls,
    None
  )
  def f1Sym = classSymbol.declaredMethod("f1").head
  def f1Def = DefDef(f1Sym,  _ => Some('{"Hello"}.asTerm))

  val classDef = ClassDef(classSymbol, parents, List(f1Def))

  val newCls   = Typed(
    Apply(
      Select(New(TypeIdent(classSymbol)), classSymbol.primaryConstructor),
      Nil
    ),
    TypeTree.of[T]
  )

  val block = Block(List(classDef), newCls)

  report.info(block.show(using Printer.TreeAnsiCode))
  // '{()}
  block.asExprOf[T]
