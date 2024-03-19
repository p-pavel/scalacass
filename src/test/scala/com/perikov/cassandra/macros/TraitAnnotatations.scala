package com.perikov.cassandra.macros
import com.perikov.cassandra.protocol.*

class TraitAnnotatations extends munit.FunSuite:
  test("no annotations") {
    val errs = compileErrors("""
      trait T:
        def f: Int = 42
      val a: T = new T {}
      val annotations = generateDispatch[T])(12, a)

    """)
    assert(
      errs.contains(
        "Expected exactly one annotation of type dispatchBy, found 0"
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

end TraitAnnotatations
