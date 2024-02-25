package com.roshan.routes

import com.roshan.models.GetRecord
import com.roshan.services.{State}
import zio.http._
import zio._
import zio.stm.{TMap, USTM}

import java.util.UUID


case class GetDetails(store:Ref[Map[UUID, Option[State]]]) {
  private def getRecord() = Routes(
    Method.GET / "getrecord" / string("id") -> handler {
      (id:String ,_:Request) =>  GetRecord.process(id,store)
    }
  )
}

object GetDetails {
  def getRoute(store:Ref[Map[UUID, Option[State]]]) = GetDetails(store).getRecord()
}

