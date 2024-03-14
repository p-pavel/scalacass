package com.perikov.bananas

import cats.arrow.Profunctor
import cats.Bifunctor
import com.perikov.bananas.ADTs



object TF extends ADTs:
  trait ProdSig[A, B]:
    type Self
    def apply(a: A, b: B): Self

  trait SumSig[A, B]:
    type Self
    def left(a: A): Self
    def right(b: B): Self

  type Alg[T[_, _] <: { type Self }, A, B] = (t: T[A, B]) => t.Self 
  override opaque type Prod[A, B] = Alg[ProdSig, A, B]
  opaque type Sum[A, B] = Alg[SumSig, A, B]

  given prodBifunctor: Bifunctor[Prod] with
    def bimap[a, b, c, d](fab: Prod[a, b])(f: a => c, g: b => d): Prod[c, d] =
      (p: ProdSig[c, d]) =>
        object p1 extends ProdSig[a, b]:
          type Self = p.Self
          def apply(a: a, b: b): p.Self = p.apply(f(a), g(b))
        fab(p1)

  given sumBifunctor: Bifunctor[Sum] with
    def bimap[a, b, c, d](fab: Sum[a, b])(f: a => c, g: b => d): Sum[c, d] =
      (s: SumSig[c, d]) =>
        object s1 extends SumSig[a, b]:
          type Self = s.Self
          def left(a: a): s.Self = s.left(f(a))
          def right(b: b): s.Self = s.right(g(b))
        fab(s1)