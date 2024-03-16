package com.roshan.routes

import com.roshan.models.SignUp
import com.roshan.services.{AtomicServices, State}
import zio.Ref
import zio.http._

import java.util.UUID
case class SignUpRoute(store:Ref[Map[UUID, Option[State]]]) {
  private def signup: Routes[AtomicServices with SignUp, Nothing] = Routes(
    Method.POST / "signup" -> handler {
      req:Request => SignUp.process(req,store)
    }
  )
}

object SignUpRoute {
  def getRoute(store:Ref[Map[UUID, Option[State]]]): Routes[AtomicServices with SignUp, Nothing] = SignUpRoute(store).signup
}
