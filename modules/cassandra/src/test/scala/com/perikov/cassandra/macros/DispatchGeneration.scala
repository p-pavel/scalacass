package com.perikov.cassandra.macros
import com.perikov.cassandra.protocol.*

class DispatchGeneration extends munit.FunSuite:
  test("no annotations") {
    val errs = compileErrors("""
      trait T:
        def f: Int = 42
      val a: T = new T {}
      val annotations = generateDispatch[T])(12, a)

    """)
    assert(
      errs.contains(
        "Expected exactly one annotation of type `dispatchBy`"
      )
    )
  }

  test("annotation with type") {
    class phony() extends annotation.StaticAnnotation
    @dispatchBy[opcode]
    trait T:
      @phony
      @opcode(10)
      def abc: Int = 42

      def cde: Float = 3.141f

    val t = new T {}

    assertEquals(generateDispatch(10, t), 42)
  }

  test("dispatch by name") {
    @dispatchByMethodName
    trait T:
      def abc: Int   = 42
      def cde: Float = 3.141f

    val t = new T {}

    assertEquals(dispatcherByMethodName("abc", t), 42)
    assertEquals(dispatcherByMethodName("cde", t), 3.141f)
  }

end DispatchGeneration