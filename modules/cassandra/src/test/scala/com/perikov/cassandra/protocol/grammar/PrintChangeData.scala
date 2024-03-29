package com.perikov.cassandra.protocol.grammar

import com.perikov.cassandra.macros.*
import annotation.experimental
trait Prt extends SchemaChangeData {
  override type Self       = String
  override type stringList = string

}

@experimental
class PrintChangeData extends munit.FunSuite:
  test("print change data") {
    val changeDataPrinter = derivePrinting[Prt]
    val args              = Seq("ks", "obj", "types")
    val it                = args.iterator

    inline given changeDataPrinter.string = it.next()

    val res = "AGGREGATE".dispatcherByMethodName(changeDataPrinter)
    assertEquals(res, args.mkString("AGGREGATE(", ", ", ")"))
  }
end PrintChangeData
