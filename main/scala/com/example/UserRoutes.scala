package com.example

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.example.UserRegistry.{ActionPerformed, CreateUser, DeleteUser, GetUser, GetUserResponse, GetUsers}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class UserRoutes(userRegistry: ActorRef[UserRegistry.Command])(implicit val
system: ActorSystem[_]) {

  implicit lazy val timeout = Timeout(10.seconds)
//  lazy val log = Logging(system, classOf[UserRoutes])

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

//  def getUsers(): ToResponseMarshallable = ???;
//
//  def createUser(user: User): OnSuccessMagnet = ???
//
//  def getUser(name: String): OnSuccessMagnet = ???
//
//  def deleteUser(name: String): OnSuccessMagnet = ???

  val userRoutes: Route =
    pathPrefix("users") {
      concat(
        pathEnd {
          concat(
              get {
                def getUsers():Future[Users] = userRegistry.ask(GetUsers(_))
                complete(getUsers() )
            },
            post {

              entity(as[User]) { user =>
                def createUser(user: User): Future[ActionPerformed] =
                  userRegistry.ask(CreateUser(user,_))
                onSuccess(createUser(user)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            })
        },
        path(Segment) { name =>
          concat(
            get {
              rejectEmptyResponse {
                def getUser(name:String): Future[GetUserResponse] =
                  userRegistry.ask(GetUser(name,_))
                onSuccess(getUser(name)) { response =>
                  complete(response.maybeUser)
                }
              }
            },
            delete {
              def deleteUser(name: String): Future[ActionPerformed] =
                userRegistry.ask(DeleteUser(name,_))
              onSuccess(deleteUser(name)) { performed =>
                complete((StatusCodes.OK, performed))
              }
            })
        })
    }


}


