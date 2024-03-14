package com.perikov.cassandra.protocol
import java.time.OffsetDateTime
import java.net.SocketAddress


trait CompisiteTypes:
  /**
    * A sequence of bytes in the ASCII range [0, 127].  Bytes with values outside of
    * this range will result in a validation error.
    */
  type ascii 
  /** any sequence of bytes */
  type blob
/** The decimal format represents an arbitrary-precision number.  It contains an
  [int] "scale" component followed by a varint encoding (see section 6.17)
  of the unscaled value.  The encoded value represents "<unscaled>E<-scale>".
  In other words, "<unscaled> * 10 ^ (-1 * <scale>)". */
  type decimal

trait PrimTypes:
  type Bytes = Array[Byte]
  /**   An eight-byte two's complement integer. */
  opaque type bigint = Long
  /** A single byte.  A value of 0 denotes "false"; any other value denotes "true".
  (However, it is recommended that a value of 1 be used to represent "true".) */
  opaque type boolean = Boolean
  /** An unsigned integer representing days with epoch centered at 2^31.
  (unix epoch January 1st, 1970) */
  opaque type date = Int 
  /** An 8 byte floating point number in the IEEE 754 binary64 format. */
  opaque type double = Double
  opaque type duration = Nothing // TODO
  opaque type float = Float
  opaque type inet = SocketAddress
  opaque type int = Int
  opaque type list = IArray[Bytes] //TODO: use ByteBuffer
  opaque type map = Map[Bytes, Bytes] //TODO: use ByteBuffer
  opaque type set = Array[Bytes] //TODO: use ByteBuffer
  opaque type smallint = Short
  opaque type text = String
  opaque type time = Nothing // TODO
  opaque type timestamp = Nothing
  opaque type timeuuid = Nothing
  opaque type tinyint = Byte
  opaque type tuple = Nothing
  opaque type uuid = Nothing
  type varchar = text
  opaque type varint = BigInt



