package com.perikov.cassandra.macros

import scala.quoted.*
import com.perikov.cassandra.protocol.*
inline def generateDispatch[T](e: Any, interpreter: T)  = ${
  traitAnnotationsImpl[T]('e, 'interpreter)
}

def traitAnnotationsImpl[T: Type](e: Expr[Any], interpreter: Expr[T])(using Quotes) =
  new AnnotationUtils {}.generateDispatch(e, interpreter)
trait AnnotationUtils(using Quotes):
  import quotes.reflect.*

  def generateDispatch[T: Type](e: Expr[Any], interpreter: Expr[T]): Expr[Any] =
    val interpreterTerm = interpreter.asTerm
    val typeReprOfTargetTrait = TypeRepr.of[T].dealias

    val targetTraitSymbol = typeReprOfTargetTrait.typeSymbol
    val dispatchBySymbol  = TypeRepr.of[dispatchBy].typeSymbol

    val annotations: List[Term] =
      targetTraitSymbol.annotations.filter(_.tpe.typeSymbol == dispatchBySymbol)

    if annotations.length != 1 then
      report.errorAndAbort(
        s"Expected exactly one annotation of type dispatchBy, found ${annotations.length}"
      )

    val symbolToDispatchBy = annotations.head.tpe.typeArgs.head.typeSymbol

    def extractAnnotationValue(t: Term) =
      t match
        case Apply(Select(New(_), _), List(arg)) => arg
        case _                                   => report.errorAndAbort(s"Unexpected annotation $t")

    val annotatedMethods: List[(Symbol, Term)] =
      targetTraitSymbol.methodMembers.map { method =>
        val annots =
          method.annotations.filter(t => symbolToDispatchBy == t.tpe.typeSymbol)
        if annots.length > 1 then
          report.errorAndAbort(
            s"Expected at most one annotation of type dispatchBy, found ${annots.length}"
          )
        if annots.isEmpty then None
        else Option(method, extractAnnotationValue(annots.head))
      }.flatten
    val cases = annotatedMethods.map((methodSymbol, pattern) =>
      CaseDef(
        pattern,
        None,
        callWithGivensImpl(
          interpreterTerm.select(methodSymbol).asExpr
        ).asTerm
      )
    )
    Match(e.asTerm, cases).asExpr
    // Expr.ofList(annotatedMethods.map(_.toString).map(Expr(_)).toList)
