package com.roshan.routes

import com.roshan.models.SignUp
import zio.http._
case class SignUpRoute() {
  def signup() = Routes(
    Method.POST / "signup" -> handler {
      req:Request => SignUp.process(req)
    }
  )
}

object SignUpRoute {
  val getRoute = SignUpRoute().signup()
}
