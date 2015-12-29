package com.catapulta.interfaces

import java.util.UUID

import com.catapulta.messages.ConnectionRequest
import com.catapulta.models.{User, UsersInterface}
import com.catapulta.utils
import mage.interfaces.MageClient
import mage.interfaces.callback.ClientCallback
import mage.remote.{Connection, SessionImpl}
import mage.utils.MageVersion
import mage.utils.MageVersion._
import scala.collection._
import scala.collection.JavaConversions._

/**
  * Created by sandromosca on 27/12/15.
  */
object UsersMemory extends UsersInterface with MageClient { // TODO: decouple MageClient
  private var users: mutable.Map[User, SessionImpl] = mutable.Map.empty

  override def pairById(id: String): Option[(User, SessionImpl)] =
    users.find({ case (user, session) => user.token.toString == id })

  override val name = "memory"

  override def getById(id: String): Option[User] = pairById(id).map(_._1)

  override def getSessionById(id: String): Option[SessionImpl] = pairById(id).map(_._2)

  override def getAll: Seq[User] = users.keys.toSeq

  override def add(request: ConnectionRequest): (User, SessionImpl) = {
    val user = User(
      request.nickname,
      request.server,
      request.port,
      UUID.randomUUID()
    )

    val session = new SessionImpl(this)
    val connection = utils.connect(user)

    session.connect(connection)

    val pair = user -> session

    users += pair

    pair
  }

  override def getVersion = new MageVersion(MAGE_VERSION_MAJOR, MAGE_VERSION_MINOR, MAGE_VERSION_PATCH, MAGE_VERSION_MINOR_PATCH, MAGE_VERSION_INFO)
  override def processCallback(callback: ClientCallback) = { println(callback.getMethod); val data = callback.getData.asInstanceOf[java.util.List[String]]; println(data.toSeq) }
  override def connected(message: String): Unit = {}
  override def disconnected(message: Boolean): Unit = {}
  override def showMessage(message: String): Unit = {}
  override def showError(message: String): Unit = {}

}
