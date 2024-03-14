package com.perikov.cassandra.protocol

trait Requests:
  def STARTUP(cqlVersion: Nothing, compression: Nothing): Any
/**
  *  Asks the server to return which STARTUP options are supported. The body of an
  OPTIONS message should be empty and the server will respond with a SUPPORTED
  message.
  */  
  def OPTIONS(): Any
  def QUERY(query: Nothing, queryParams: Nothing): Any
  /**
    * The server will respond with a RESULT message with a `prepared` kind (0x0004,
  see Section 4.2.5).
    *
    * @param query is a [long string] representing the CQL query. 
    * @param keyspace
    * @return
    */
  def PREPARE(query: Nothing, keyspace: Option[Nothing]): Any
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
  def EXECUTE(id: Nothing, resultMetadataId: Nothing, queryParameters: Nothing): Any
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
  def REGISTER(params: Nothing): Any
  def BATCH(params: Nothing): Any
  /**
    * //  The response to a AUTH_RESPONSE is either a follow-up AUTH_CHALLENGE message,
    *  an AUTH_SUCCESS message or an ERROR message.
    */
  def AUTH_RESPONSE(token: Nothing): Any
end Requests