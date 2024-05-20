package com.roshan.models

import com.roshan.services.SubscriptionType.Inactive
import com.roshan.services.{AtomicServices, State}
import zio._
import zio.http.{Request, Response}
import zio.json.{DecoderOps, DeriveJsonDecoder, DeriveJsonEncoder, EncoderOps, JsonDecoder, JsonEncoder}

import java.util.UUID

case class SignUp() {
  private def validate(state: State): ZIO[Any, String, State] = {
    val pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$".r
    if (pattern.matches(state.PassWord)) {
      ZIO.succeed(state)
    } else {
      ZIO.fail("Not a valid password")
    }
  }

  def addMember(state: State,store:Ref[Map[UUID, Option[State]]]): ZIO[AtomicServices, String, UUID] = for {
    serv <- ZIO.service[AtomicServices]
    st <- validate(state: State)
    id <- serv.insertData(Some(st),store)
    _ <- ZIO.log(s"inserted data to store for id $id")
  } yield id

}

object SignUp {
  val live: ZLayer[Any, Nothing, SignUp] = ZLayer.fromFunction(SignUp.apply _)

  def process(request: Request,store:Ref[Map[UUID, Option[State]]]): ZIO[AtomicServices with SignUp, Nothing, Response] = {

    val p = for {
      serv <- ZIO.service[SignUp]
      strReq <- request.body.asString
      obj <- ZIO.fromEither(strReq.fromJson[SignUpReq])
      state = State(None, obj.FirstName, obj.LastName, obj.UserName, obj.PassWord, obj.age, Some(Inactive))
      id <- serv.addMember(state,store)
    } yield id

    p.foldZIO(e => ZIO.succeed(Response.json(SignUpResFailure(e.toString).toJson)), id => ZIO.succeed(Response.json(SignUpResSuccess(id).toJson)))
  }
}


case class SignUpReq(FirstName: String, LastName: String, UserName: String, PassWord: String, age: Int)

object SignUpReq {
  implicit val encoding: JsonEncoder[SignUpReq] = DeriveJsonEncoder.gen[SignUpReq]
  implicit val decoding: JsonDecoder[SignUpReq] = DeriveJsonDecoder.gen[SignUpReq]
}


case class SignUpResSuccess(id: UUID, message: String = "welcome to the family", success: Boolean = true)

object SignUpResSuccess {
  implicit val encoding: JsonEncoder[SignUpResSuccess] = DeriveJsonEncoder.gen[SignUpResSuccess]
  implicit val decoding: JsonDecoder[SignUpResSuccess] = DeriveJsonDecoder.gen[SignUpResSuccess]
}

case class SignUpResFailure(error: String, success: Boolean = false)

object SignUpResFailure {
  implicit val encoding: JsonEncoder[SignUpResFailure] = DeriveJsonEncoder.gen[SignUpResFailure]
  implicit val decoding: JsonDecoder[SignUpResFailure] = DeriveJsonDecoder.gen[SignUpResFailure]
}