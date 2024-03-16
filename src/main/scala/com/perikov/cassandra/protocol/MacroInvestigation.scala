package com.perikov.cassandra.protocol

import com.perikov.cassandra.macros.printArgs
object Something:
  def meth(x: IArray[Byte]): Int = x.length

@main
def macroInvestigation() = 
  given x: IArray[Byte] = IArray(1, 2, 3)
  println(printArgs(Something.meth))
//Inlined(Some(TypeIdent("Macros$package$")), Nil, TypeApply(Select(Select(Select(Ident("scala"), "compiletime"), "package$package"), "summonInline"), List(TypeIdent("Int"))))