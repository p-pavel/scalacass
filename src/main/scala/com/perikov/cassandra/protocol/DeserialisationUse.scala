package com.perikov.cassandra.protocol

import java.nio.ByteBuffer

trait Proto:
  def header(
      verion: Byte,
      flags: Byte,
      stream: Short,
      opcode: Byte,
      length: Int,
      rest: ByteBuffer
  ): Unit


object Proto:
  object Printer extends Proto:
    def header(
        verion: Byte,
        flags: Byte,
        stream: Short,
        opcode: Byte,
        length: Int,
        rest: ByteBuffer
    ): Unit =
      println(
        s"verion: $verion, flags: $flags, stream: $stream, opcode: $opcode, length: $length, buf: $buf"
      )

  given buf: java.nio.ByteBuffer = Samples.headerByteBuffer()
  inline given String = "You're screwed"
  

trait MyFunc:
  def apply(a: Int, b: Float): Unit

object Q:
  import java.nio.*
  val b = ByteBuffer.allocate(8)
  b.putInt(42)
  b.putFloat(3.141f)
  b.flip()

  val myFunc: MyFunc             = (a: Int, b: Float) => println(s"Int: $a, Float: $b")
  val ff: (Int, Float) => String = (a: Int, b: Float) => s"Int: $a, Float: $b"

  @main def test =
    inline given Int   = b.getInt
    inline given Float = b.getFloat
    // println(callWithGivens(myFunc))
    // println(callWithGivens(ff) )
    println(callWithGivens((a: Int, b: Float) => b + a))
