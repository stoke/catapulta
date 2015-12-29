package com.catapulta.services

import java.util.UUID

import com.catapulta.messages._
import com.catapulta.implicits._
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Response, Request}
import com.catapulta.interfaces.UsersMemory
import com.twitter.util.FuturePool

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._


/**
  * Created by sandromosca on 27/12/15.
  */
object ConnectionService {
  def service: Service[Request, Response] = createConnection.toService

  val createConnection: Endpoint[Token] = post("connection" ? connectionRequestReader) { request: ConnectionRequest =>
    println(s"Received ${request.nickname}")

    FuturePool.unboundedPool {
      val pair = UsersMemory.add(request)
      val token = Token(pair._1.token)

      Ok(token)
    }

  }

}
