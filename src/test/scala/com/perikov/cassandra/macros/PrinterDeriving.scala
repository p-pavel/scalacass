package com.perikov.cassandra.macros

import scala.annotation.experimental


@experimental
class PrinterDeriving extends munit.FunSuite:
  test("deriving printing") {
    trait Abc:
      def f1(g: Int, a: String): String
      // def f2(t: Float, a: String): String
    end Abc

    val a: Abc= derivePrinting[Abc]
    assertEquals(a.f1(10,"s"),"Hello")

    // assertEquals(a.f1(10, "SDF"), "f1(10, \"SDF\")")
    // assertEquals(a.f2(0f, "SDF"), "f2(0.0, \"SDF\")")

  }
