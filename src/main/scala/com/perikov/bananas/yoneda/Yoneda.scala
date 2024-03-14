package com.perikov.bananas.yoneda

import cats.CoflatMap.ops


type Hom = Any {type Dom; type Codom}



trait Category:
  type H <: Hom
  type Endo = H {type Codom = Dom }
  def leftId(h: H): Endo {type Dom = h.Dom}
  def rightId(h: H): Endo {type Dom = h.Codom}
  def compose(f: H, g: H {type Codom = f.Dom}): H {type Dom = g.Dom; type Codom = f.Codom}

trait Profunctor:
  val Dom: Hom
  val Codom: Hom
  type Dom = Dom.type
  type Codom = Codom.type




type Morphism[H <: Hom, A, B] =
  (h: H { type Codom = A }) => H  {type Dom = h.Dom; type Codom = B} 

trait Functor extends Any:
  type F[_]
  type Dom <: Hom
  type Codom <: Hom
  def map[A, B](f: Morphism[Dom, A, B]): Morphism[Codom, F[A], F[B]]


object Morphism:
  def id[H <: Hom, A]: Morphism[H, A, A] = (h: H { type Codom = A }) => h

  extension [H <: Hom, B, C](f: Morphism[H, B, C])
    def ∘[A](g: Morphism[H, A, B]): Morphism[H, A, C] =
      (h: H { type Codom = A }) => f(g(h))



