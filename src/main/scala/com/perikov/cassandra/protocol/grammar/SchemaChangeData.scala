package com.perikov.cassandra.protocol.grammar

import com.perikov.cassandra.protocol.*

@dispatchByMethodName
trait SchemaChangeData extends BasicTypes:
  type Self

  def KEYSPACE(keyspace: string): Self
  
  def TABLE(keyspace: string, objectName: string): Self

  /** stands for modifications related to user types */
  def TYPE(keyspace: string, objectName: string): Self

  /** stands for modifications related to user defined functions */
  def FUNCTION(keyspace: string, objectName: string, types: stringList): Self

  /** stands for modifications related to user defined aggregates */
  def AGGREGATE(keyspace: string, objectName: string, types: stringList): Self
end SchemaChangeData
