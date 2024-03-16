package com.perikov.cassandra.macros

import scala.quoted.*

transparent inline def printArgs(inline t: Any) = ${ printArgsImpl('t) }

def printArgsImpl(func: Expr[Any])(using Quotes): Expr[Any] =
  import quotes.*
  import quotes.reflect.*

  val term     = func.asTerm
  val tpe      = term.tpe
  if (!tpe.isFunctionType) then
    report.errorAndAbort(
      s"${term.show(using Printer.TreeAnsiCode )}: ${tpe.show(using Printer.TypeReprAnsiCode)} is not a function type"
    )
  val firstArg = tpe.typeArgs.head
  Implicits.search(firstArg) match
    case ok: ImplicitSearchSuccess   => ok.tree.asExpr
    case fail: ImplicitSearchFailure => report.errorAndAbort(fail.explanation)
