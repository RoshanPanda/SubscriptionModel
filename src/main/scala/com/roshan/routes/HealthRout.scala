package com.roshan.routes


import zio.ZLayer
import zio.http._

case class HealthRout() {
  def health(): Routes[Any, Nothing] = Routes(
    Method.GET / "health" -> handler(Response.text("i am doing good !!!!"))
  )
}

object HealthRout {
  val getRoute: Routes[Any, Nothing] =  HealthRout().health()
}
