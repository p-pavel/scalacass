package com.perikov.cassandra.protocol

class errorCode(code: Int) extends annotation.StaticAnnotation

trait Errors:
  type T
  type consistency
  type reasonMap
  type int
  type short
  type string
  type writeType
  type byte
  type stringList
  type shortBytes

  /** Server error: something unexpected happened. This indicates a server-side
    * bug.
    */
  @errorCode(0x0000)
  def serverError: T

  /** Protocol error: some client message triggered a protocol violation (for
    * instance a QUERY message is sent before a STARTUP one has been sent).
    */
  @errorCode(0x000a)
  def protocolError: T

  /** Authentication error: authentication was required and failed. The possible
    * reason for failing depends on the authenticator in use, which may or may
    * not include more detail in the accompanying error message.
    */
  @errorCode(0x0100)
  def authenticationError: T

  /** @param requestedCL
    *   the consistency level of the query that triggered the error
    * @param required
    *   requested number of nodes to satisfy the consistency level
    * @param alive
    *   number of replicas that were known to be alive when the request was
    *   processed
    */
  @errorCode(0x1000)
  def unavailableException(
      requestedCL: consistency,
      required: Int,
      alive: Int
  ): T

  /** Overloaded: the request cannot be processed because the coordinator node
    * is overloaded
    */
  @errorCode(0x1001)
  def overloaded: T

  /** the request was a read request but the coordinator node is bootstrapping
    */
  @errorCode(0x1002)
  def isBootstrapping: T

  /** error during a truncation error. */
  @errorCode(0x1003)
  def truncateError: T

  /** @param cl
    *   level of the query having triggered the exception.
    * @param received
    *   the number of nodes having acknowledged the request
    * @param blockFor
    *   the number of replicas whose acknowledgement is required to achieve
    *   consistency
    * @param writeType
    *   type of the write that timed out
    * @param contentions
    *   present if the write type is CAS
    */
  @errorCode(0x1100)
  def writeTimeout(
      cl: consistency,
      received: int,
      blockFor: int,
      writeType: writeType,
      contentions: Option[short]
  ): T

  /** Read_timeout: Timeout exception during a read request.
    *
    * @param cl
    *   is the consistency level of the query having triggered the exception.
    * @param received
    *   the number of nodes having answered the request.
    * @param blockFor
    *   the number of replicas whose response is required to achieve cl.
    * @param dataPresent
    *
    * @note
    *   Please note that it is possible to have `received` >= `blockfor` if
    *   `data_present` is `false`. Also in the (unlikely) case where `cl` is
    *   achieved but the coordinator node times out while waiting for
    *   read-repair acknowledgement.
    */
  @errorCode(0x1200)
  def readTimeout(
      cl: consistency,
      received: int,
      blockFor: int,
      dataPresent: byte
  ): T

  /** A non-timeout exception during a read request
    *
    * @param cl
    *   level of the query having triggered the exception.
    * @param received
    *   the number of nodes having answered the request.
    * @param blockFor
    *   the number of replicas whose acknowledgement is required to achieve `cl`
    * @param reasonMap
    *   a map of endpoint to failure reason codes. This maps the endpoints of
    *   the replica nodes that failed when executing the request to a code
    *   representing the reason for the failure.
    * @param dataPresent
    *   value is 0, it means the replica that was asked for data had not
    *   responded. Otherwise, the value is != 0.
    */
  @errorCode(0x1300)
  def readFailure(
      cl: consistency,
      received: int,
      blockFor: int,
      reasonMap: reasonMap,
      dataPresent: byte
  ): T

  /** A (user defined) function failed during execution.
    * @param keyspace
    *   keyspace of failed function
    * @param function
    *   name of failed function
    * @param argTypes
    *   one string for each argument type (as CQL type) of the failed function
    */
  @errorCode(0x1400)
  def functionFailure(
      keyspace: string,
      function: string,
      argTypes: stringList
  ): T

  /** A non-timeout exception during a write request.
    *
    * @param cl
    *   level of the query having triggered the exception.
    * @param received
    *   the number of nodes having answered the request.
    * @param blockFor
    *   the number of replicas whose acknowledgement is required to achieve
    *   `cl`.
    * @param reasonMap
    *   a map of endpoint to failure reason codes.
    * @param writeType
    *   the type of the write that failed.
    */
  @errorCode(0x1500)
  def writeFailure(
      cl: consistency,
      received: int,
      blockFor: int,
      reasonMap: reasonMap,
      writeType: writeType
  ): T

  /** @todo not specified in docs */
  @errorCode(0x1600)
  def CDC_WRITE_FAILURE: T

  /** An exception occured due to contended Compare And Set write/update. The
    * CAS operation was only partially completed and the operation may or may
    * not get completed by the contending CAS write or SERIAL/LOCAL_SERIAL read.
    *
    * @param cl
    * @param received
    * @param blockFor
    */
  @errorCode(0x1700)
  def CAS_WRITE_UNKNOWN(cl: consistency, received: int, blockFor: int): T

  /** The submitted query has a syntax error.
    */
  @errorCode(0x2000)
  def syntaxError: T

  /** The logged user doesn't have the right to perform the query.
    */
  @errorCode(0x2100)
  def unauthorized: T

  /** The query is syntactically correct but invalid. */
  @errorCode(0x2200)
  def invalid: T

  /** The query is invalid because of some configuration issue
    */
  @errorCode(0x2300)
  def configError: T

  /** The query attempted to create a keyspace or a table that was already
    * existing
    *
    * @param keyspace
    *   either the keyspace that already exists, or the keyspace in which the
    *   table that already exists is.
    * @param table
    *   the name of the table that already exists. If the query was attempting
    *   to create a keyspace, <table> will be present but will be the empty
    *   string.
    */
  @errorCode(0x2400)
  def alreadyExists(keyspace: string, table: string): T

  /** Can be thrown while a prepared statement tries to be executed if the
    * provided prepared statement ID is not known by this host. The rest of the
    * ERROR message body will be [short bytes] representing the unknown ID.
    */
  @errorCode(0x2500)
  def unprepared(unknownId: shortBytes): T

end Errors
