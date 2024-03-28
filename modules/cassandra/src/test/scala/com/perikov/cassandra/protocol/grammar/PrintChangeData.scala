package com.perikov.cassandra.protocol.grammar

import com.perikov.cassandra.macros.derivePrinting
import annotation.experimental
trait Prt     extends SchemaChangeData{
  override type Self = String
  // override type string = String
  override type stringList = Any

  // def AGGREGATE(`keyspace₄`: string, `objectName₃`: string, types: stringList): Self 
  // def FUNCTION(keyspace: string, objectName: string, types: stringList): Self
  // def f(s: stringList): Self

}

@experimental
class PrintChangeData extends munit.FunSuite:
  test("print change data") {
    // val changeDataPrinter = derivePrinting[Prt]

    // assertEquals(changeDataPrinter.AGGREGATE("ks", "obj", "types"), "AGGREGATE(ks, obj, types)")
  }
end PrintChangeData
