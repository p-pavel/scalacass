package com.perikov.cassandra.macros

import scala.annotation.experimental

@experimental
class PrinterDeriving extends munit.FunSuite:
  trait Abc:
    def f1(g: Int, a: String): String
    def f2(t: Float, a: String): String
  end Abc

  test("deriving printing") {
    val a: Abc = derivePrinting[Abc]
    assertEquals(a.f1(10, "s"), "f1(10, s)")
    assertEquals(a.f2(0f, "s"), "f2(0.0, s)")
  }

  test("type aliasing") {
    type T = Abc
    val a: T = derivePrinting[T]
    assertEquals(a.f1(10, "s"), "f1(10, s)")
  }

  test("tagless final") {
    trait Q[T]:
      def f1(g: Int, a: String): T

    val a: Q[String] = derivePrinting[Q[String]]
  }

end PrinterDeriving
