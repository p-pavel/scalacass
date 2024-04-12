package com.perikov.cassandra.macros

import scala.annotation.experimental

class opcode(c: Int) extends scala.annotation.StaticAnnotation


trait Protocol:
  def query1(a: Int, b: String): Unit


trait Writer[T] extends (T => Unit)

@experimental
class SerializerDeriving extends munit.FunSuite: 
  test("Serializer deriving") {
    var serialized = Vector.empty[Any]
    given [A]: Writer[A] with
      def apply(a: A) = serialized = serialized :+ a

    val p = deriveSerializer[Protocol]
    p.query1(42, "hello")
    assertEquals(serialized, Vector(42))
  }

