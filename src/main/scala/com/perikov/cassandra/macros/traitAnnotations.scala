package com.perikov.cassandra.macros

import scala.quoted.*
import com.perikov.cassandra.protocol.*

extension (expr: Any)
  transparent inline def dispatchTo[T](interpreter: T) = 
    generateDispatch(expr, interpreter)

// TODO: I hate the amount of code here and duplication of concepts.
// Probably use something like term deriving on annotation type to 
// find the implementation of generator

transparent inline def generateDispatch[T](e: Any, interpreter: T) = ${
  traitAnnotationsImpl[T]('e, 'interpreter)
}

inline def dispatcherByMethodName[T](s: String, interpreter: T) = ${
  dispatchByMethodNameImpl[T]('s, 'interpreter)
}

def dispatchByMethodNameImpl[T: Type](s: Expr[String], interpreter: Expr[T])(
    using Quotes
) =
  DispatchGenerator(s, interpreter).generateDispatchByMethodName

def traitAnnotationsImpl[T: Type](e: Expr[Any], interpreter: Expr[T])(using
    Quotes
) =
  DispatchGenerator(e, interpreter).generateDispatch

private class DispatchGenerator[T: Type](
    selector: Expr[Any],
    val interpreter: Expr[T]
)(using val q: Quotes):
  import quotes.reflect.*

  val interpreterTerm            = interpreter.asTerm
  val typeReprOfTargetTrait      = TypeRepr.of[T].dealias
  val targetTraitSymbol          = typeReprOfTargetTrait.typeSymbol
  def annotationsWith(s: Symbol) =
    targetTraitSymbol.annotations.filter(_.tpe.typeSymbol == s)

  def generateDispatchByMethodName =
    val dispatchBySymbol = TypeRepr.of[dispatchByMethodName].typeSymbol
    val annotations      = annotationsWith(dispatchBySymbol)
    if annotations.length != 1 then
      report.warning(
        s"Expected exactly one annotation of type dispatchByMethodName, found ${annotations.length}"
      )
    val declaredMethods  = targetTraitSymbol.declaredMethods
    val cases            = declaredMethods.map(m =>
      CaseDef(
        Literal(StringConstant(m.name)),
        None,
        callWithGivensImpl(interpreterTerm.select(m).asExpr).asTerm
      )
    )
    Match(selector.asTerm, cases).asExpr
  end generateDispatchByMethodName

  def generateDispatch: Expr[Any] =

    val dispatchBySymbol        = TypeRepr.of[dispatchBy].typeSymbol
    val annotations: List[Term] = annotationsWith(dispatchBySymbol)

    if annotations.length != 1 then
      report.errorAndAbort(
        s"Expected exactly one annotation of type `dispatchBy` for $targetTraitSymbol, found ${annotations.length}"
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
          try
            interpreterTerm.select(methodSymbol).etaExpand(Symbol.spliceOwner).asExpr
          catch case e: Exception =>
            report.errorAndAbort(
              s"Failed to select method ${methodSymbol.name} from interpreter: ${interpreterTerm}:\n${e.getMessage()}"
            )
        ).asTerm
      )
    )
    Match(selector.asTerm, cases).asExpr
  end generateDispatch

end DispatchGenerator
