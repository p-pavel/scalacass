package com.perikov.cassandra.protocol

import scala.quoted.*


//TODO: consider pure way to get values from givens:
// collect values in predictable order and then apply them to the function

transparent inline def callWithGivens(inline f: Any) =
  ${ callWithGivensImpl('f) }

def callWithGivensImpl(func: Expr[Any])(using Quotes): Expr[Any] =
  import quotes.*
  import quotes.reflect.*

  val funcTerm = func.asTerm

  // TODO: use summonInline instead?
  def findImplicitFor(s: Symbol): Term =
    val requiredType = s.termRef.typeSymbol
    Implicits.search(requiredType.typeRef) match
      case iss: ImplicitSearchSuccess => iss.tree.asExpr.asTerm
      case isf: ImplicitSearchFailure =>
        report.errorAndAbort(
          s"Could not find implicit of type '${requiredType}' for parameter '${s.name}' in ${func.show}: ${func.asTerm.tpe.dealias}"
        )

  def applyToImplicits(t: Term, params: List[Symbol]): Apply =
    t.appliedToArgs(params.map(findImplicitFor))

  val select: Term = funcTerm.asApplication

  select.getEtaExpandedParameterTypeSymbols
    .foldLeft(select)(applyToImplicits)
    .asExpr

extension (using q: Quotes)(term: q.reflect.Term)
  def asApplication: q.reflect.Select =
    val applySymbols = term.tpe.typeSymbol.methodMember("apply")
    if applySymbols.length != 1 then
      q.reflect.report.errorAndAbort(
        s"Expected exactly one 'apply' method in ${term.show}: ${term.tpe.typeSymbol}"
      )

    term.select(applySymbols.head)

  def getEtaExpandedParameterTypeSymbols: List[List[q.reflect.Symbol]] =
    import quotes.reflect.*
    term.etaExpand(term.symbol) match
      case b @ Block(List(DefDef(_, params, _, _)), _) =>
        params.map(_.params.map(_.asInstanceOf[ValDef].tpt.symbol))
      case _                                           =>
        report.errorAndAbort(
          s"Error trying to eta-expand ${term.show(using Printer.TreeAnsiCode)}"
        )
