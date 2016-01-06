package com.catapulta.mageclient

import com.catapulta.events.StartGame
import com.catapulta.EventManager
import com.catapulta.models.User
import mage.interfaces.MageClient
import mage.interfaces.callback.ClientCallback
import mage.utils.MageVersion
import mage.utils.MageVersion._
import mage.view.TableClientMessage
import scala.collection.JavaConversions._

/**
  * Created by sandromosca on 31/12/15.
  */
class Client(val user: User) extends MageClient {
  override def getVersion = new MageVersion(
    MAGE_VERSION_MAJOR, MAGE_VERSION_MINOR, MAGE_VERSION_PATCH, MAGE_VERSION_MINOR_PATCH, MAGE_VERSION_INFO
  )

  implicit val userImplicit = user

  override def processCallback(callback: ClientCallback) = {
    val event = callback.getMethod match {
      case "startGame" =>
        val clientMessage = callback.getData.asInstanceOf[TableClientMessage]

        println("Dispatching StartGame")

        EventManager add StartGame(clientMessage.getGameId.toString)
    }

  }

  override def connected(message: String): Unit = {}
  override def disconnected(message: Boolean): Unit = {}
  override def showMessage(message: String): Unit = {}
  override def showError(message: String): Unit = {}
}
