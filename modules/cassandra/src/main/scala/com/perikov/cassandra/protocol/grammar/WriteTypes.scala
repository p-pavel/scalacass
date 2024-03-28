package com.perikov.cassandra.protocol.grammar

import com.perikov.cassandra.protocol.*
import com.perikov.cassandra.macros.dispatchByMethodName

/** @todo
  *   descriptions are taken from error messages
  */
@dispatchByMethodName
trait WriteTypes:
  type Res

  /** the write was a non-batched non-counter write. */
  def SIMPLE: Res

  /** the write was a (logged) batch write. If this type is received, it means
    * the batch log has been successfully written (otherwise a "BATCH_LOG" type
    * would have been sent instead)
    */
  def BATCH: Res

  /** the write was an unlogged batch. No batch log write has been attempted.
    */
  def UNLOGGED_BATCH: Res

  /** the write was a counter write (batched or not).
    */
  def COUNTER: Res

  /** the timeout occurred during the write to the batch log when a (logged)
    * batch write was requested
    */
  def BATCH_LOG: Res

  /** the timeout occured during the Compare And Set write/update. */
  def CAS: Res

  /** the timeout occured when a write involves VIEW update and failure to
    * acqiure local view(MV) lock for key within timeout
    */
  def VIEW: Res

  /** the failure occured when cdc_total_space_in_mb is exceeded when doing a
    * write to data tracked by cdc.
    */
  def CDC: Res
end WriteTypes
