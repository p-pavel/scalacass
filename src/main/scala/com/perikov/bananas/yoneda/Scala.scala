package com.perikov.bananas.yoneda

//TODO: Zero overhead
trait Scala extends Any:
  type Dom
  type Codom
  def apply(a: Dom): Codom

object Scala:
  def id[A] = Impl[A, A](identity)
  
  def fromScala(s: Scala): Morphism[Scala, s.Dom, s.Codom] =
    (h: Scala { type Codom = s.Dom }) => Impl(a => s(h(a)))

  def toScala[A, B](
      m: Morphism[Scala, A, B]
  ): Scala { type Dom = A; type Codom = B } =
    m(Scala.id)


class Impl[A, B](f: A => B) extends AnyVal, Scala:
    type Dom = A
    type Codom = B
    def apply(a: A): B = f(a)
