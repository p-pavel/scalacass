package com.perikov.cassandra.protocol.grammar


object BasicTypes:
  /**  A consistency level specification. This is a [short]
                    representing a consistency level with the following
                    correspondance:

                      - 0x0000    ANY
                      - 0x0001    ONE
                      - 0x0002    TWO
                      - 0x0003    THREE
                      - 0x0004    QUORUM
                      - 0x0005    ALL
                      - 0x0006    LOCAL_QUORUM
                      - 0x0007    EACH_QUORUM
                      - 0x0008    SERIAL
                      - 0x0009    LOCAL_SERIAL
                      - 0x000A    LOCAL_ONE
                      */
  opaque type consistency = Short
  
  /**
  is a map of endpoint to failure reason codes. This maps
                            the endpoints of the replica nodes that failed when
                            executing the request to a code representing the reason
                            for the failure. The map is encoded starting with an [int] n
                            followed by n pairs of <endpoint><failurecode> where
                            <endpoint> is an [inetaddr] and <failurecode> is a [short].
                            */
  opaque type reasonMap = Map[Any,Any]
  type int = Int
  type short = Short
  type string = String
  

  type writeType = ((w: WriteTypes) => w.Res)
  type byte = Byte

  type stringList  = Seq[String]
  type shortBytes = IArray[Byte]
  type stringMultimap = Map[String, Seq[String]]
  type stringMap = Map[String, String]
  type bytes = IArray[Byte]
  type inet = String

end BasicTypes
