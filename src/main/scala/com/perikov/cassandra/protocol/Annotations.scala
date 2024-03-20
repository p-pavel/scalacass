package com.perikov.cassandra.protocol

import scala.annotation.StaticAnnotation

/** @note
  *   there's a security risk that every method declared in the annotated trait
  *   may be called by it's name
  */
class dispatchByMethodName   extends StaticAnnotation
class dispatchBy[Annotation] extends StaticAnnotation

class opcode(code: Byte)    extends StaticAnnotation
class errorCode(code: Int)  extends StaticAnnotation
class resultCode(code: Int) extends StaticAnnotation
