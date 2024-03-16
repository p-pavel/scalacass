package com.perikov.cassandra.macros

import scala.quoted.*

/**
  * Having something resembling function generate a call to 
  * it with parameters derived from givens
  *
  * @param t function or method
  * @return the result of function call
  */
transparent inline def callWithGivens(t: Any) = ${ callWithGivensImpl('t) }

private def callWithGivensImpl(func: Expr[Any])(using Quotes): Expr[Any] =
  import quotes.*
  import quotes.reflect.*

  val term     = func.asTerm
  val tpe      = term.tpe.widen
  if (!tpe.isFunctionType) then
    report.errorAndAbort(
      s"${term.show(using Printer.TreeStructure)}: ${tpe.show(using Printer.TypeReprStructure)} is not a function type"
    )
  val firstArg = tpe.typeArgs.head

  def findImplicit(arg: TypeRepr): Term =
    Implicits.search(arg) match
      case iss: ImplicitSearchSuccess => iss.tree
      case isf: ImplicitSearchFailure =>
        report.errorAndAbort(
          s"Could not find implicit of type '${arg}' for parameter in ${func.show}: ${tpe}"
        )

  val argsWithoutReturnType = tpe.typeArgs.dropRight(1).map(findImplicit)
  val applySymbols          = tpe.typeSymbol.methodMember("apply")
  assert(
    applySymbols.length == 1,
    s"Expected exactly one 'apply' method for function ${func.show}"
  )
  val application           =
    term.select(applySymbols.head).appliedToArgs(argsWithoutReturnType)
  application.asExpr
