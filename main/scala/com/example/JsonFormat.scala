package com.example

import com.example.UserRegistry.ActionPerformed
import spray.json.DefaultJsonProtocol

import scala.collection.immutable

object JsonFormats {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._
  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}



