package com.perikov.bananas

import cats.arrow.Profunctor
import cats.Bifunctor

object Scala extends ADTs:
  override type Prod[+A, +B] = (A, B)
  override type Sum[+A, +B] = Either[A, B]
  override given sumBifunctor: Bifunctor[Sum] = Bifunctor.catsBifunctorForEither
  override given prodBifunctor: Bifunctor[Prod] =
    Bifunctor.catsBifunctorForTuple2