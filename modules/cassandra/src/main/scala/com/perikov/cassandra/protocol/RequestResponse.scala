package com.perikov.cassandra.protocol

trait IndexedMonad[F[_, _, _]]:
  extension [A](a: A)
    def pure[S]: F[S, S, A]

  extension [S1, S2, A](fa: F[S1,S2,A])
    def flatMap[S3, B]( f: A => F[S2, S3, B]): F[S1, S3, B]
    def map[B](f: A => B): F[S1, S2, B] = fa.flatMap(a => f(a).pure)

trait RequestResponse[F[_, _, _]]:
  type AuthToken
  type BeforeStartup
  def queryOptions: F[BeforeStartup, BeforeStartup, Unit]
  def startup(
      options: Map[String, String]
  ): F[BeforeStartup, BeforeStartup, Option[AuthToken]]

