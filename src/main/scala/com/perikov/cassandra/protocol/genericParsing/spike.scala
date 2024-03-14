package com.perikov.cassandra.protocol.genericParsing

import com.perikov.cassandra.protocol.Responses

trait CassandraProto:
  type Header
  type Opcode
  type Length = Int

  def header(opcode: Opcode, length: Length): Header

trait ParsingPrims:
  type Res
  type Dispatcher[Selector]
  type Repeated[Count]

