package com.perikov.cassandra.protocol

import scala.annotation.StaticAnnotation

class dispatchByMethodName   extends StaticAnnotation
class dispatchBy[Annotation] extends StaticAnnotation

class opcode(code: Byte)    extends StaticAnnotation
class errorCode(code: Int)  extends StaticAnnotation
class resultCode(code: Int) extends StaticAnnotation
