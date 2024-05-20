package com.roshan


import caliban._
import caliban.interop.tapir.HttpInterpreter
import caliban.schema.GenericSchema
import com.roshan.models.{GetRecord, SignUp}
import com.roshan.routes.{GetDetails, HealthRout, SignUpRoute, graphQlResolvers}
import com.roshan.services.{AtomicServices, State}
import zio._
import zio.http.Middleware.basicAuth
import zio.http.{HttpApp, Method, Routes, SSLConfig, Server}

import java.util.UUID
import scala.language.postfixOps

object main extends ZIOAppDefault {

  import sttp.tapir.json.jsoniter._

  private def https(store:Ref[Map[UUID, Option[State]]]): ZIO[Any, CalibanError.ValidationError, HttpApp[AtomicServices with GetRecord with SignUp]] =
    for {
    h <- ZIO.succeed(HealthRout.getRoute.toHttpApp)
    s <- ZIO.succeed(SignUpRoute.getRoute(store).toHttpApp)
    g <- ZIO.succeed(GetDetails.getRoute(store).toHttpApp)
    graph <- interpreter(store)
  } yield h ++ s ++ g ++ graph @@ basicAuth("admin", "admin")

  private object schema extends GenericSchema[AtomicServices]
  private def interpreter(store:Ref[Map[UUID, Option[State]]]): ZIO[Any, CalibanError.ValidationError, HttpApp[AtomicServices]] = {
    import schema.auto._
    import caliban.schema.ArgBuilder.auto._

    for {
    interpreter <- graphQL(graphQlResolvers.rootResolvers(store)).interpreter
    route <- ZIO.succeed(Routes(
        Method.ANY / "api" / "graphql" -> ZHttpAdapter.makeHttpService(HttpInterpreter(interpreter))
      ).toHttpApp)
  } yield (route)
  }

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

  private val app = for {
    ref <- Ref.make[Map[UUID, Option[State]]](Map(initid -> None))
    htt <- https(ref)
  } yield htt


  private val httpApp: ZIO[Any, CalibanError.ValidationError, ZIO[Any, Throwable, Nothing]] = for {
    r <- app
  } yield Server.serve(r).provide(Server.live, configLayer, signUpLayers, getDetailsLayers, attomicLive)


  def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Nothing] = httpApp.flatten
}
