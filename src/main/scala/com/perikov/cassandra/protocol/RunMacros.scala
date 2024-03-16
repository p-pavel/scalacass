package com.perikov.cassandra.protocol
import cats.arrow.Profunctor
import cats.Bifunctor

class Fix[F[_]](val unfix: F[Fix[F]])
type Kind1 <: { type F[_] }

infix type ∘[H[_], G[_]] = Kind1 { type F[A] = H[G[A]] }

type Kind2 <: { type F[_, _] }
// infix type ∘∘[H[_], G[_]] = Kind2 { type F[A, B] = H[G[A, B]] }

trait TC:
  type Self

trait option:
  type A
  type Self
  def none: Self
  def some(a: A): Self

trait pair:
  type A
  type B
  type Self
  def apply(a: A, b: B): Self

trait snoc extends Any:
  type A
  type B
  type Self
  def nil: Self
  def snoc(list: B, elem: A): Self

type Snoc  = Any {
  type A <: {type Res}
  type B
  type Self
  def nil: Self
  def snoc(list: B, elem: A): Self
  def depMethod(using a: A, b: a.Res): Int

} 

type fixSnoc[A] = snoc { type B = Self }


@main def testInfo = println(traitInfo[Snoc])

// TypeTree[
//   TypeBounds(
//     TypeRef(ThisType(TypeRef(NoPrefix,module class scala)),class Nothing),
//     RecType(
//       RefinedType(
//         RefinedType(
//           RefinedType(
//             RefinedType(
//               RefinedType(
//                 RefinedType(
//                   TypeRef(
//                     TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),class Any),A,TypeBounds(TypeRef(ThisType(TypeRef(NoPrefix,module class scala)),class Nothing),RefinedType(TypeRef(ThisType(TypeRef(NoPrefix,module class lang)),class Object),Res,TypeBounds(TypeRef(ThisType(TypeRef(NoPrefix,module class scala)),class Nothing),TypeRef(ThisType(TypeRef(NoPrefix,module class scala)),class Any))))),B,TypeBounds(TypeRef(ThisType(TypeRef(NoPrefix,module class scala)),class Nothing),TypeRef(ThisType(TypeRef(NoPrefix,module class scala)),class Any))),Self,TypeBounds(TypeRef(ThisType(TypeRef(NoPrefix,module class scala)),class Nothing),TypeRef(ThisType(TypeRef(NoPrefix,module class scala)),class Any))),nil,ExprType(TypeRef(RecThis(1389590001),Self))),snoc,MethodType(List(list, elem), List(TypeRef(RecThis(1389590001),B), TypeRef(RecThis(1389590001),A)), TypeRef(RecThis(1389590001),Self))),depMethod,ContextualMethodType(List(a, b), List(TypeRef(RecThis(1389590001),A), TypeRef(TermParamRef(a),Res)), TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),class Int))) | 1389590001)
//     )
//   ]