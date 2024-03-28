package com.perikov.cassandra.macros

import scala.annotation.StaticAnnotation

/** @note
  *   there's a security risk that every method declared in the annotated trait
  *   may be called by it's name
  */
class dispatchByMethodName   extends StaticAnnotation
class dispatchBy[Annotation] extends StaticAnnotation
