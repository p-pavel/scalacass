package com.perikov.cassandra.protocol.grammar

import com.perikov.cassandra.protocol.*

@dispatchBy[opcode]
trait Requests:
  type Self

  @opcode(0x01)
  def STARTUP(cqlVersion: Nothing, compression: Nothing): Self
/**
  *  Asks the server to return which STARTUP options are supported. The body of an
  OPTIONS message should be empty and the server will respond with a SUPPORTED
  message.
  */  
  @opcode(0x05)
  def OPTIONS: Self
  
  @opcode(0x07)
  def QUERY(query: Nothing, queryParams: Nothing): Self
  /**
    * The server will respond with a RESULT message with a `prepared` kind (0x0004,
  see Section 4.2.5).
    *
    * @param query is a [long string] representing the CQL query. 
    * @param keyspace
    * @return
    */
  @opcode(0x09)
  def PREPARE(query: Nothing, keyspace: Option[Nothing]): Self
  /**
    *  Executes a prepared query. The body of the message must be:
  `<id><result_metadata_id><query_parameters>`
    *
    * @param id is the prepared query ID. It's the [short bytes] returned as a
      response to a PREPARE message
    * @param resultMetadataId is the ID of the resultset metadata that was sent
      along with response to PREPARE message. If a RESULT/Rows message reports
      changed resultset metadata with the Metadata_changed flag, the reported new
      resultset metadata must be used in subsequent executions.
    * @param queryParameters has the exact same definition as in QUERY (see Section 4.1.4)
    * @return
    */
  @opcode(0x0a)
  def EXECUTE(id: Nothing, resultMetadataId: Nothing, queryParameters: Nothing): Self
  /**
    *  Register this connection to receive some types of events. The body of the
  message is a [string list] representing the event types to register for. See
  section 4.2.6 for the list of valid event types.

  The response to a REGISTER message will be a READY message.

  Please note that if a client driver maintains multiple connections to a
  Cassandra node and/or connections to multiple nodes, it is advised to
  dedicate a handful of connections to receive events, but to *not* register
  for events on all connections, as this would only result in receiving
  multiple times the same event messages, wasting bandwidth.
    *
    * @param params
    * @return
    */
  @opcode(0x0b)
  def REGISTER(params: Nothing): Self

  @opcode(0x0d)
  def BATCH(params: Nothing): Self

  /**
    * //  The response to a AUTH_RESPONSE is either a follow-up AUTH_CHALLENGE message,
    *  an AUTH_SUCCESS message or an ERROR message.
    */
  @opcode(0x10)
  def AUTH_RESPONSE(token: Nothing): Self
end Requests