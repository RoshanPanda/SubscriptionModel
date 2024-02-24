package com.roshan.services

import zio.stm._
import zio._

import java.util.UUID
case class AtomicServices() {
  def insertData(state:Option[State]): ZIO[Any, Nothing, UUID] = for {
    tmp <- AtomicServices.DStore.commit
    rnd <- ZIO.random
    id <-  rnd.nextUUID
    _  <- tmp.put(id,state.map(s => s.copy(id=Some(id)))).commit
  } yield id

  def getData(id:UUID): ZIO[Any, Nothing, Option[State]] = for {
    tmp <- AtomicServices.DStore.commit
    data   <- tmp.get(id).commit
  } yield data.flatten

  def deleteData(id:UUID): ZIO[Any, Nothing, Unit] = for {
    tmp <- AtomicServices.DStore.commit
    _   <- tmp.delete(id).commit
  } yield ()

  def updateData(id:UUID,state:State): ZIO[Any, Nothing, Unit] = for {
    tmp <- AtomicServices.DStore.commit
    _ <- tmp.updateWith(id)(e => e.map(_ => Some(state))).commit
  } yield()

}

object AtomicServices {

  private val DStore: USTM[TMap[UUID, Option[State]]] = TMap.empty
  val live: ZLayer[Any, Nothing, AtomicServices] = ZLayer.fromFunction(AtomicServices.apply _)
 // val LiveDStore: ULayer[USTM[TMap[UUID, Some[State]]]] = ZLayer.succeed(DStore)
}



trait SubscriptionType
case object Active extends SubscriptionType
case object Inactive extends SubscriptionType

case class State(id:Option[UUID], FirstName:String, LastName:String, UserName:String, PassWord:String, age:Int, subscription:Option[SubscriptionType])
object State {
  def live(id:Option[UUID], FirstName:String, LastName:String, UserName:String, PassWord:String, age:Int, subscription:Option[SubscriptionType]) = ZLayer.succeed(State(id:Option[UUID], FirstName:String, LastName:String, UserName:String, PassWord:String, age:Int, subscription:Option[SubscriptionType]))
}

