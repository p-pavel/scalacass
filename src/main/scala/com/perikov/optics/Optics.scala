package com.perikov.optics

trait Lens[A, B, S, T]:
  def view(s: S): A
  def update(b: B, s: S): T
  def modify(f: A => B): S => T = s => update(f(view(s)), s)

given firstL[A, B, C]: Lens[A, B, (A, C), (B, C)] with
  def view(s: (A, C)): A              = s._1
  def update(b: B, s: (A, C)): (B, C) = (b, s._2)

inline given positive: Lens[Boolean, Boolean, Int, Int] with
  inline def view(s: Int): Boolean           = s > 0
  inline def update(b: Boolean, s: Int): Int = if b then s.abs else -s.abs

extension [S](s: S)
  inline def update[B, T](b: B)(using l: Lens[?, B, S, T]): T = l.update(b, s)

