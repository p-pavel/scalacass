package com.perikov.cassandra.macros

import scala.quoted.*
import scala.annotation.experimental

@FunctionalInterface
trait Writer[T] extends (T => Unit)
