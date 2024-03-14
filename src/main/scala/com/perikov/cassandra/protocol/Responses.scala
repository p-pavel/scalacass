package com.perikov.cassandra.protocol

class opcode(code: Byte) extends annotation.StaticAnnotation

trait Responses:
  type Res 
  type Content
  type ErrorContent
  type string
  type int = Int
  type stringMultimap
  type bytes

  /**
    * Indicates an error processing a request. The body of the message will be an
  error code ([int]) followed by a [string] error message. Then, depending on
  the exception, more content may follow. The error codes are defined in
  Section 9, along with their additional content if any.
    *
    * @param code
    * @param message
    * @param rest
    */
  @opcode(0x00)
  def ERROR(code: int, message: string, rest: ErrorContent): Res

  /**
    *   Indicates that the server is ready to process queries. This message will be
  sent by the server either after a STARTUP message if no authentication is
  required (if authentication is required, the server indicates readiness by
  sending a AUTH_RESPONSE message).

  The body of a READY message is empty.
    */
  @opcode(0x02)
  def READY: Res
  
    /**
    Indicates that the server requires authentication, and which authentication
  mechanism to use.

  The authentication is SASL based and thus consists of a number of server
  challenges (AUTH_CHALLENGE, Section 4.2.7) followed by client responses
  (AUTH_RESPONSE, Section 4.1.2). The initial exchange is however boostrapped
  by an initial client response. The details of that exchange (including how
  many challenge-response pairs are required) are specific to the authenticator
  in use. The exchange ends when the server sends an AUTH_SUCCESS message or
  an ERROR message.

  This message will be sent following a STARTUP message if authentication is
  required and must be answered by a AUTH_RESPONSE message from the client.

  The body consists of a single [string] indicating the full class name of the
  IAuthenticator in use.
    */
  @opcode(0x03)
  def AUTHENTICATE(authClass: string): Res
  
  
  /**
    *   Indicates which startup options are supported by the server. This message
  comes as a response to an OPTIONS message.

  The body of a SUPPORTED message is a [string multimap]. This multimap gives
  for each of the supported STARTUP options, the list of supported values. It
  also includes:
      - "PROTOCOL_VERSIONS": the list of native protocol versions that are
      supported, encoded as the version number followed by a slash and the
      version description. For example: 3/v3, 4/v4, 5/v5-beta. If a version is
      in beta, it will have the word "beta" in its description.
    *
    */
  @opcode(0x06)
  def SUPPORTED(options: stringMultimap): Res
  
  /**
    *   The result to a query (QUERY, PREPARE, EXECUTE or BATCH messages).

  The first element of the body of a RESULT message is an [int] representing the
  `kind` of result. The rest of the body depends on the kind. The kind can be
  one of:

    - 0x0001    Void: for results carrying no information.
    - 0x0002    Rows: for results to select queries, returning a set of rows.
    - 0x0003    Set_keyspace: the result to a `use` query.
    - 0x0004    Prepared: result to a PREPARE message.
    - 0x0005    Schema_change: the result to a schema altering query.

  The body for each kind (after the [int] kind) is defined below.
    */
  @opcode(0x08)
  def RESULT(kind: int, rest: Content): Res

  /**
    * An event pushed by the server. A client will only receive events for the
  types it has REGISTERed to. The body of an EVENT message will start with a
  [string] representing the event type. The rest of the message depends on the
  event type. The valid event types are:
    - "TOPOLOGY_CHANGE": events related to change in the cluster topology.
      Currently, events are sent when new nodes are added to the cluster, and
      when nodes are removed. The body of the message (after the event type)
      consists of a [string] and an [inet], corresponding respectively to the
      type of change ("NEW_NODE" or "REMOVED_NODE") followed by the address of
      the new/removed node.
    - "STATUS_CHANGE": events related to change of node status. Currently,
      up/down events are sent. The body of the message (after the event type)
      consists of a [string] and an [inet], corresponding respectively to the
      type of status change ("UP" or "DOWN") followed by the address of the
      concerned node.
    - "SCHEMA_CHANGE": events related to schema change. After the event type,
      the rest of the message will be <change_type><target><options> where:
        - <change_type> is a [string] representing the type of changed involved.
          It will be one of "CREATED", "UPDATED" or "DROPPED".
        - <target> is a [string] that can be one of "KEYSPACE", "TABLE", "TYPE",
          "FUNCTION" or "AGGREGATE" and describes what has been modified
          ("TYPE" stands for modifications related to user types, "FUNCTION"
          for modifications related to user defined functions, "AGGREGATE"
          for modifications related to user defined aggregates).
        - <options> depends on the preceding <target>:
          - If <target> is "KEYSPACE", then <options> will be a single [string]
            representing the keyspace changed.
          - If <target> is "TABLE" or "TYPE", then
            <options> will be 2 [string]: the first one will be the keyspace
            containing the affected object, and the second one will be the name
            of said affected object (either the table, user type, function, or
            aggregate name).
          - If <target> is "FUNCTION" or "AGGREGATE", multiple arguments follow:
            - [string] keyspace containing the user defined function / aggregate
            - [string] the function/aggregate name
            - [string list] one string for each argument type (as CQL type)

  All EVENT messages have a streamId of -1 (Section 2.3).

  Please note that "NEW_NODE" and "UP" events are sent based on internal Gossip
  communication and as such may be sent a short delay before the binary
  protocol server on the newly up node is fully started. Clients are thus
  advised to wait a short time before trying to connect to the node (1 second
  should be enough), otherwise they may experience a connection refusal at
  first.
    *
    */
  @opcode(0x0C)
  def EVENT(eventType: string, rest: Content): Res
  
  /**
    *  A server authentication challenge (see AUTH_RESPONSE (Section 4.1.2) for more
  details).

  The body of this message is a single [bytes] token. The details of what this
  token contains (and when it can be null/empty, if ever) depends on the actual
  authenticator used.

  Clients are expected to answer the server challenge with an AUTH_RESPONSE
  message.
    *
    */
  @opcode(0x0E)
  def AUTH_CHALLENGE(token: bytes): Res
  
  /**
    *   Indicates the success of the authentication phase. See Section 4.2.3 for more
  details.

  The body of this message is a single [bytes] token holding final information
  from the server that the client may require to finish the authentication
  process. What that token contains and whether it can be null depends on the
  actual authenticator used.
    *
    */
  @opcode(0x10)
  def AUTH_SUCCESS(token: bytes): Res
