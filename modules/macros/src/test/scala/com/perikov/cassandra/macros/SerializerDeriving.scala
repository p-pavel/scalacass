package com.perikov.cassandra.macros

import scala.annotation.experimental



trait Protocol:
  def query1(a: Int, b: String): Unit



@experimental
class SerializerDeriving extends munit.FunSuite: 

  test("Serializer deriving") {
    var serialized = Vector.empty[Any]
    def append(a: Any) = serialized = serialized :+ a
    given  Writer[Int] = append
    given Writer[String] = append


    val p = deriveSerializer[Protocol]
    p.query1(42, "hello")
    assertEquals(serialized, Vector(42, "hello"))
  }

