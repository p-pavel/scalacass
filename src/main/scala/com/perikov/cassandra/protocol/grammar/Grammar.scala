package com.perikov.cassandra.protocol.grammar
import java.nio.ByteBuffer

trait Grammar extends Any, BasicTypes:
  self =>

  type Self

  val results: Results
  val errors: Errors
  val changeData: SchemaChangeData

  val events: Events {
    type ChangeData = changeData.Self
  }

  val responses: Responses {
    type Self          = self.Self
    type ErrorContent  = errors.Self
    type EventContent  = events.Self
    type ResultContent = results.Self
  }

  opaque type Header = byte

  given (using b: ByteBuffer): byte   = b.get()
  given (using b: ByteBuffer): short  = b.getShort()
  given (using b: ByteBuffer): int    = b.getInt()
  given (using b: ByteBuffer): string =
    val len   = b.getShort()
    val bytes = new Array[Byte](len)
    b.get(bytes)
    String(bytes, "UTF-8")

  

  given (using b: ByteBuffer): bytes = ???
    // TODO: IArray creation, nulls
    // val len = b.getInt()
    // if len >= 0 then
    //   val bytes = new Array[Byte](len)
    //   b.get(bytes)
    //   bytes
    //   len
    // else 0

  private def header(
      version: byte,
      flags: byte,
      stream: short,
      opcode: byte,
      length: int
  ): Header =
    require(version == 0x85, s"Invalid protocol version: $version")
    opcode

  def message(header: Header)(using rest: ByteBuffer): Self =
    // summon[bytes]
    // header.dispatchTo(responses)
    ???

  // def parse(using b: ByteBuffer): Self =
  //   val hdr = callWithGivens(header)
  //   message(hdr)

end Grammar

class decoders(using b: ByteBuffer) extends BasicTypes

// transparent inline def decs(using  ByteBuffer) = new decoders
