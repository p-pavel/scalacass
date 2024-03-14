package com.perikov.cassandra.protocol

import scala.quoted.*


inline def traitInfo[T <: AnyKind]: String  = ${traitInfoImpl[T]}

def traitInfoImpl[T <: AnyKind:Type](using Quotes): Expr[String]  = 
  import quoted.*
  import quotes.reflect.*

  def processParam(p: ValDef | TypeDef) = 
    val name = p.name
    p.match 
      case v: ValDef => 
        val tpe = v.tpt
        s"Param $name: ${tpe.show(using Printer.TreeAnsiCode)}"
      case t: TypeDef => s"I can't process typedef ${t.show(using Printer.TreeAnsiCode)}"

  def processStatement(d: Statement) = 
    d match
      case DefDef(name, params, typeTree, optBody) => 
        val clauses = params.map(_.params.map(processParam)).mkString("(", ", ", ")")
        s"I found method $name with param clauses ${clauses}"
      case a => s"I don't know what to do with  ${a.show(using Printer.TreeAnsiCode)}" 

  val tpe = TypeRepr.of[T]

  tpe.dealias.typeSymbol.tree match
    case cd@ClassDef(name, _, parents, _, stmts) =>
      val parent = parents.headOption
      val stmtsString = stmts.map(processStatement).mkString("\n")
      val ansiTree = s"ClassDef ${cd.show(using Printer.TreeAnsiCode)}"
      val res = Seq(stmtsString, ansiTree).mkString("\n-------\n\n")
      Expr(res)
    case td: TypeDef =>
      val ansiTree = s"TypeDef ${td.show(using Printer.TreeAnsiCode)}"
      val tr = td.rhs
      val trRepr = tr.toString()
      Expr(trRepr)
    case s => Expr(s"Unknown $s")

