@main
def test = println("Hello, world!")

trait Responses:
  def ERROR: Any
  def READY: Any
  def AUTHENTICATE: Any
  def SUPPORTED: Any
  def RESULT: Any
  def EVENT: Any
  def AUTH_CHALLENGE: Any
  def AUTH_SUCCESS: Any

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

trait Opcodes:
  type T
  def ERROR: T
  def STARTUP: T
  def READY: T
  def AUTHENTICATE: T
  def OPTIONS: T
  def SUPPORTED: T
  def QUERY: T
  def RESULT: T
  def PREPARE: T
  def EXECUTE: T
  def REGISTER: T
  def EVENT: T
  def BATCH: T
  def AUTH_CHALLENGE: T
  def AUTH_RESPONSE: T
  def AUTH_SUCCESS: T

trait OpcodesCodes extends Opcodes:
  override opaque type T = Byte
  override def ERROR: T = 0x00
  override def STARTUP: T = 0x01
  override def READY: T = 0x02
  override def AUTHENTICATE: T = 0x03
  override def OPTIONS: T = 0x05
  override def SUPPORTED: T = 0x06
  override def QUERY: T = 0x07
  override def RESULT: T = 0x08
  override def PREPARE: T = 0x09
  override def EXECUTE: T = 0x0a
  override def REGISTER: T = 0x0b
  override def EVENT: T = 0x0c
  override def BATCH: T = 0x0d
  override def AUTH_CHALLENGE: T = 0x0e
  override def AUTH_RESPONSE: T = 0x0f
  override def AUTH_SUCCESS: T = 0x10

trait Protocol[T]:
  def version(isRequest: Boolean): Int
