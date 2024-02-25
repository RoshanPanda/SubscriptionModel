package com.roshan.services


import zio.stm._
import zio._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.util.UUID
 case class AtomicServices() { //store:TRef[Map[UUID, Option[State]]]
  def insertData(state:Option[State],store:Ref[Map[UUID, Option[State]]]): ZIO[Any, Nothing, UUID] = for {
    tmp <- store.get//.commit
    _ <-  ZIO.log(s"initial ids ${tmp.keys}")//tmp.get.commit.flatMap(v => ZIO.log(s"initial id is ${v}"))
    rnd <- ZIO.random
    id <-  rnd.nextUUID
    _  <-  store.update(m => m + (id -> state.map(s => s.copy(id=Some(id)))))//.commit
    _  <- ZIO.log(s"after insert ids ${tmp.keys}")//tmp.get.commit.flatMap(v => ZIO.log(s"value inserted is ${v.get(id)}")).debug
    _ <- store.get.map(_.keys).flatMap(v => ZIO.log(s"final ids ${v}"))//tmp.get.commit.flatMap(v => ZIO.log(s"final ids are  ${v.keys}"))
  } yield id

  def getData(id:UUID,store:Ref[Map[UUID, Option[State]]]): ZIO[Any, Nothing, Option[State]] = for {
    tmp <- store.get//.commit
    _ <-  ZIO.log(s"initial ids ${tmp.keys}")//tmp.get.commit.flatMap(v => ZIO.log(s"ids are  ${v.keys}"))
    _   <- ZIO.log(s"looking for id $id")
    //mp   <- tmp.get.commit
    data <- ZIO.succeed(tmp.get(id))
  } yield data.flatten

  def deleteData(id:UUID,store:USTM[TMap[UUID, Option[State]]]): ZIO[Any, Nothing, Unit] = for {
    tmp <- store.commit
    _   <- tmp.delete(id).commit
  } yield ()

  def updateData(id:UUID,state:State,store:USTM[TMap[UUID, Option[State]]]): ZIO[Any, Nothing, Unit] = for {
    tmp <- store.commit
    _ <- tmp.updateWith(id)(e => e.map(_ => Some(state))).commit
  } yield()

}


object AtomicServices {

  val live = ZLayer.fromFunction(AtomicServices.apply _ )
  //val LiveDStore: ULayer[USTM[TMap[UUID, Option[State]]]] = ZLayer.succeed(DStore)
}


sealed trait SubscriptionType extends Product with Serializable

case object Active extends SubscriptionType

case object Inactive extends SubscriptionType

object SubscriptionType {
  implicit val encode: JsonEncoder[SubscriptionType] = DeriveJsonEncoder.gen[SubscriptionType]
  implicit val decode: JsonDecoder[SubscriptionType] = DeriveJsonDecoder.gen[SubscriptionType]
}

case class State(id:Option[UUID], FirstName:String, LastName:String, UserName:String, PassWord:String, age:Int, subscription:Option[SubscriptionType])
object State {
  implicit val encoder: JsonEncoder[State] = DeriveJsonEncoder.gen[State]
  implicit val decoder: JsonDecoder[State] = DeriveJsonDecoder.gen[State]
  def live(id:Option[UUID], FirstName:String, LastName:String, UserName:String, PassWord:String, age:Int, subscription:Option[SubscriptionType]) = ZLayer.succeed(State(id:Option[UUID], FirstName:String, LastName:String, UserName:String, PassWord:String, age:Int, subscription:Option[SubscriptionType]))
}

