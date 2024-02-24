package com.roshan

import com.roshan.models.SignUp
import com.roshan.routes.{HealthRout, SignUpRoute}
import com.roshan.services.AtomicServices
import zio._
import zio.http.Middleware.basicAuth
import zio.http.{HttpApp, SSLConfig, Server}

object main extends ZIOAppDefault {

  private val helthRoute = HealthRout.getRoute.toHttpApp //@@ basicAuth("admin", "admin")
  private val signUproute = SignUpRoute.getRoute.toHttpApp

  val app = helthRoute ++ signUproute @@ basicAuth("admin", "admin")

  private val sslConfig = SSLConfig.fromResource(
    behaviour = SSLConfig.HttpBehaviour.Accept,
    certPath = "server.crt",
    keyPath = "server.key",
  )

  private val config = Server.Config.default
    .port(8090)
    .ssl(sslConfig)

  private val configLayer = ZLayer.succeed(config)

  private val signUpLayers = SignUp.live ++ AtomicServices.live //++ AtomicServices.LiveDStore

  def run = Server.serve(app).provide(Server.live,configLayer,signUpLayers)
}
