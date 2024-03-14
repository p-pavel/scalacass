package com.perikov.bananas.yoneda

object TypeAlgebra:

  opaque type Phantom[+T] = Unit


type Request = Any {
  type Bye
  type Hello[A <: Bye] <: Bye
}

trait Q:
  type A = {type Res}
  def TF[T <: Nothing ?=> Any](t: T)  = ???
  def s(using a: A, b: a.Res)(c: Int): String

