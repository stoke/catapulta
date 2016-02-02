package com.catapulta.interfaces

import java.util.UUID

import com.catapulta.messages.ConnectionRequest
import com.catapulta.models.{User, UsersInterface}
import com.catapulta.utils
import com.catapulta.mageclient.Client
import mage.remote.SessionImpl
import scala.collection._

/**
  * Created by sandromosca on 27/12/15.
  */
object UsersMemory extends UsersInterface {
  private var users: concurrent.TrieMap[User, SessionImpl] = concurrent.TrieMap.empty

  override def pairById(id: String): Option[(User, SessionImpl)] =
    users.find({ case (user, session) => user.token.toString == id })

  override val name = "memory"

  override def getById(id: String): Option[User] = pairById(id).map(_._1)

  override def getSessionById(id: String): Option[SessionImpl] = pairById(id).map(_._2)

  override def getAll: Seq[User] = users.keys.toSeq

  override def add(request: ConnectionRequest): (User, SessionImpl) = {
    val user = User(
      request.nickname,
      request.password,
      request.email,
      request.server,
      request.port,
      UUID.randomUUID()
    )

    val client = new Client(user)

    val session = new SessionImpl(client)
    val connection = utils.connect(user)

    session.connect(connection)

    val pair = user -> session

    users += pair

    pair
  }

}
