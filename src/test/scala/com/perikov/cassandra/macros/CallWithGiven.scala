package com.perikov.cassandra.macros

class CallWithGiven extends munit.FunSuite:
  object Abc:
    def f(x: Int): Int = x + 1

  test("Method") {

    given a: Int = 42
    assertEquals(callWithGivens(Abc.f), Abc.f(a))
  }
  test("Inline function") {
    inline def f(x: Int): Int = x + 1
    given a: Int              = 42
    assertEquals(callWithGivens(f), f(a))
  }
  test("literal function") {
    given a: Int = 42
    assertEquals(callWithGivens((n: Int) => n + 1), 43)

  }
  test("function value") {

    val f: Function1[Int, Int] = Abc.f
    given a: Int               = 42
    assertEquals(callWithGivens(f), f(a))
  }

  test("multi param") {
    def f(x: Int, y: Long) = x + y
    given a: Int = 42
    given b: Long = 1
    assertEquals(callWithGivens(f), f(a,b))

  }

  test("non function") {

    val errs = compileErrors("""
       val f: Function1[Int, Int] = Abc.f
       given a: Int = 42
       printArgs(12)
    """)

    assertNotEquals(
      errs,
      """|error: 12: 12 is not a function type
       |    val errs = compileErrors(
       |""".stripMargin
    )
  }
