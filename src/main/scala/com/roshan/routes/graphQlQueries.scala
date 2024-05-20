package com.roshan.routes
import caliban.RootResolver
import com.roshan.models.SignUpReq
import com.roshan.services.SubscriptionType.Inactive
import com.roshan.services.{AtomicServices, State}
import zio.{Ref, URIO, ZIO}

import java.util.UUID

object graphQlQueries {
  case class Id(id: String)

  case class signupReq(signUp:SignUpReq)

  case class Queries(
                      details: Id => URIO[AtomicServices,Option[State]],
                    )

  case class Mutations(
                        signUp:SignUpReq => URIO[AtomicServices,UUID]
                      )
}

object graphQlResolvers {

  import graphQlQueries._

  def queryResolver(store: Ref[Map[UUID, Option[State]]]): Queries = Queries(
    details = id => ZIO.environment[AtomicServices].flatMap {
      e =>
        e.get.getData(UUID.fromString(id.id),store)
    }
  )

  def mutaionsResolver(store: Ref[Map[UUID, Option[State]]]): Mutations = Mutations(
    signUp =  signupReq => {
      val st  = State(None, signupReq.FirstName, signupReq.LastName, signupReq.UserName, signupReq.PassWord, signupReq.age, Some(Inactive))
      for {
        sv <- ZIO.service[AtomicServices]
        id <- sv.insertData(Some(st),store)
      } yield(id)
    }
  )

  def rootResolvers(store: Ref[Map[UUID, Option[State]]]): RootResolver[Queries, Mutations, Unit] = {
    RootResolver(queryResolver(store),mutaionsResolver(store))
  }

}

