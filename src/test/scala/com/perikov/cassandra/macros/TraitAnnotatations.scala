package com.perikov.cassandra.macros
import com.perikov.cassandra.protocol.*

class TraitAnnotatations extends munit.FunSuite:
  test("no annotations") {
    trait T:
      def f: Int
    val annotations = traitAnnotations[T]
    assert(annotations.isEmpty)
  }

  test("annotation with type") {

    @dispatchBy[opcode]
    trait T:
      def abc: Int

    assertEquals(traitAnnotations[T], List("new com.perikov.cassandra.protocol.dispatchBy[com.perikov.cassandra.protocol.opcode]()"))
  }

end TraitAnnotatations
