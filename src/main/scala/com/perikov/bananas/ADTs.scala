package com.perikov.bananas

import cats.arrow.Profunctor
import cats.Bifunctor


trait ADTs:
  type Prod[A, B]
  type Sum[A, B]

  given sumBifunctor: Bifunctor[Sum]
  given prodBifunctor: Bifunctor[Prod]

class Fix[F[_]](val unfix: F[Fix[F]]) extends AnyVal

trait RecursiveTypes extends ADTs:
  type Option[A] = Sum[Unit, A]
  type Id[A] = A

  type List[A] = Fix[[t] =>> Option[Prod[A, t]]]
  type Tree[A] = Fix[[t] =>> Option[Sum[Prod[A, A], t]]]

object RecursiveTypes:
  def apply(a: ADTs): RecursiveTypes {
    type Prod[A, B] = a.Prod[A, B]; type Sum[A, B] = a.Sum[A, B]
  } =
    new RecursiveTypes:
      export a.{*, given}

trait ProdCombinators[Prod[_, _]]:

  infix type ||[A, B]
  extension [X, A](f: X => A)
    def △[B](g: X => B): X => Prod[A, B]
    def ||[Y, B](g: Y => B): (X, Y) => Prod[A, B]

  extension [A, B](p: Prod[A, B])
    def left: A
    def right: B

