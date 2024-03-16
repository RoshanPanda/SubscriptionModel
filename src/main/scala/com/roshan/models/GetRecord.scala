package com.roshan.models

import com.roshan.services.{AtomicServices, State}
import zio.http.Response
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, EncoderOps, JsonDecoder, JsonEncoder}
import zio.{Ref, ZIO, ZLayer}

import java.util.UUID

case class GetRecord() {
  def getRec(id:String,store:Ref[Map[UUID, Option[State]]]): ZIO[AtomicServices, Nothing, Option[State]] = for {
    serv <- ZIO.service[AtomicServices]
    st <- serv.getData(UUID.fromString(id),store)
  } yield st
}

object GetRecord {

  val live: ZLayer[Any, Nothing, GetRecord] = ZLayer.fromFunction(GetRecord.apply _)
  def process(id: String,store:Ref[Map[UUID, Option[State]]]): ZIO[AtomicServices with GetRecord, Nothing, Response] = {
     for {
    serv <- ZIO.service[GetRecord]
    st <- serv.getRec(id,store)
  } yield st.fold(Response.json(getDetailsNotAvailable(id).toJson))(st => Response.json(st.toJson))

  }
}

case class getDetailsNotAvailable(id:String,message:String = "Details not available")
object getDetailsNotAvailable {
  implicit val encoder: JsonEncoder[getDetailsNotAvailable] = DeriveJsonEncoder.gen[getDetailsNotAvailable]
  implicit val decoder: JsonDecoder[getDetailsNotAvailable] = DeriveJsonDecoder.gen[getDetailsNotAvailable]
}

