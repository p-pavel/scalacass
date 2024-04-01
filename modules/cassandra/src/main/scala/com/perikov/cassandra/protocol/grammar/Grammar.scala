package com.perikov.cassandra.protocol.grammar
import com.perikov.cassandra.macros.*
import java.nio.ByteBuffer

trait Grammar extends  Any:
  self =>

  import BasicTypes.*
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

  given eventContent: events.Self = ???
  given errorContent: errors.Self = ???
  given resultContent: results.Self = ???
  given BasicTypes.stringMultimap = ???

  
  type bytes = Array[Byte]

  given (using b: ByteBuffer): bytes = 
    val len = b.getInt()
    if len >= 0 then
      val bytes = new Array[Byte](len)
      b.get(bytes)
      bytes
    else null 

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
    summon[bytes]
    // header.dispatchTo(responses)
    ???

  def parse(using b: ByteBuffer): Self =
    val hdr = callWithGivens(header)
    message(hdr)

end Grammar


