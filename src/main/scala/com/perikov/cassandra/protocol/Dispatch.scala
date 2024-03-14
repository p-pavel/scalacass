package com.perikov.cassandra.protocol

import scala.quoted.*

extension (a: Any)
  inline transparent def dispatchTo[T](b: T) = ${
    dispatchToImpl('a, 'b)
  }

def dispatchToImpl(a: Expr[Any], b: Expr[Any])(using Quotes): Expr[Any] =
  import quotes.*
  import quotes.reflect.*

  val opcodeSymbol = TypeRepr.of[opcode].typeSymbol

  def getDispatchExpr(annot: Term): Option[Expr[Any]] =
    annot.asExpr match // TODO: Any expression, not just Byte
      case '{ opcode(${ Expr[Byte](code) }) } => Some(Expr(code))
      case _                                  => report.errorAndAbort(s"Unexpected annotation: ${annot.show}")

  def getCaseDef(method: Symbol): Option[CaseDef] =
    method
      .getAnnotation(opcodeSymbol)
      .flatMap(getDispatchExpr)
      .map { pattern =>
        val call = b.asTerm.select(method)
        val rhs =
          call.etaExpand(call.symbol) match
            case b: Block => callWithGivensImpl(b.asExpr).asTerm
            case _        => call
        CaseDef(pattern.asTerm, None, rhs)
      }

  val cases = b.asTerm.tpe.typeSymbol.methodMembers.flatMap(s =>
    getCaseDef(s).toList
  )
  if cases.isEmpty then
    report.errorAndAbort(s"No cases found for ${b.asTerm.tpe.typeSymbol}")

  Match(a.asTerm, cases).asExpr
