package com.perikov.cassandra.macros

import scala.quoted.*
import com.perikov.cassandra.protocol.*
inline def traitAnnotations[T]: List[String] = ${traitAnnotationsImpl[T]}

def traitAnnotationsImpl[T: Type](using Quotes): Expr[List[String]] =
  import quotes.reflect.*
  val tpe = TypeRepr.of[T].dealias
  
  val sym = tpe.typeSymbol
  val dispatchBy = TypeRepr.of[dispatchBy].typeSymbol
  val annotations: List[Term] = sym.annotations.filter(_.tpe.typeSymbol == dispatchBy)
  Expr.ofList(annotations.map(_.show).map(Expr(_)).toList)

  
