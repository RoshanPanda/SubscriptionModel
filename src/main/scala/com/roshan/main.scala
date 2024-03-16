package com.roshan


import com.roshan.models.{GetRecord, SignUp}
import com.roshan.routes.{GetDetails, HealthRout, SignUpRoute}
import com.roshan.services.{AtomicServices, State}
import zio._
import zio.http.Middleware.basicAuth
import zio.http.{HttpApp, SSLConfig, Server}

import java.util.UUID

object main extends ZIOAppDefault {

  private def https(store:Ref[Map[UUID, Option[State]]]): ZIO[Any, Nothing, HttpApp[AtomicServices with GetRecord with SignUp]] = for {
    h <- ZIO.succeed(HealthRout.getRoute.toHttpApp)
    s <- ZIO.succeed(SignUpRoute.getRoute(store).toHttpApp)
    g <- ZIO.succeed(GetDetails.getRoute(store).toHttpApp)
  } yield h ++ s ++ g @@ basicAuth("admin", "admin")

  private val sslConfig = SSLConfig.fromResource(
    behaviour = SSLConfig.HttpBehaviour.Accept,
    certPath = "server.crt",
    keyPath = "server.key",
  )

  private val config = Server.Config.default
    .port(8090)
    .ssl(sslConfig)

  private val configLayer = ZLayer.succeed(config)

  private val signUpLayers = SignUp.live

  private val getDetailsLayers = GetRecord.live

  private val attomicLive = AtomicServices.live

  private val initid = UUID.randomUUID()

  private val app: ZIO[Any, Nothing, HttpApp[AtomicServices with GetRecord with SignUp]] = for {
    ref <- Ref.make[Map[UUID, Option[State]]](Map(initid -> None))
    htt <- https(ref)
  } yield htt

  private val httpApp: ZIO[Any, Nothing, ZIO[Any, Throwable, Nothing]] = for {
    r <- app
  } yield Server.serve(r).provide(Server.live, configLayer, signUpLayers, getDetailsLayers, attomicLive)
  def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Nothing] = httpApp.flatten
}
