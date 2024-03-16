package com.perikov.cassandra.protocol.grammar


trait BasicTypes extends Any:
  type consistency
  type reasonMap
  type int = Int
  type short = Short
  type string = String
  type writeType
  type byte = Byte
  type stringList
  type shortBytes
  type stringMultimap
  type stringMap
  type bytes = IArray[Byte]
  type inet

end BasicTypes
