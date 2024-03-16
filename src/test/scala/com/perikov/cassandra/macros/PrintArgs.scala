package com.perikov.cassandra.macros

class PrintArgs extends munit.FunSuite:
  object Abc:
    def f(x: Int): Int = x +1

  test("Method") {


    given a: Int = 42
    assertEquals(printArgs(Abc.f), 42)
  }
  test("Inline function") {
    inline def f(x: Int): Int = x + 1
    given a: Int = 42
    assertEquals(printArgs(f), 42)
  }
  test("literal function") {
    given a: Int = 42
    assertEquals(printArgs((n: Int) => n + 1), 42)
  
  }
  test("function value") {
 
    val errs = compileErrors("""
       val f: Function1[Int, Int] = Abc.f
       given a: Int = 42
       printArgs(f)
    """)

    assertEquals(errs, "")
  }
