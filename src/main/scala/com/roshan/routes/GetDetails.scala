package com.roshan.routes

import com.roshan.models.GetRecord
import com.roshan.services.{AtomicServices, State}
import zio._
import zio.http._

import java.util.UUID


case class GetDetails(store:Ref[Map[UUID, Option[State]]]) {
  private def getRecord: Routes[AtomicServices with GetRecord, Nothing] = Routes(
    Method.GET / "getrecord" / string("id") -> handler {
      (id:String ,_:Request) =>  GetRecord.process(id,store)
    }
  )
}

object GetDetails {
  def getRoute(store:Ref[Map[UUID, Option[State]]]): Routes[AtomicServices with GetRecord, Nothing] = GetDetails(store).getRecord
}

