package com.catapulta.models

import java.util.UUID

import com.catapulta.messages.ConnectionRequest
import mage.remote.SessionImpl

/**
  * Created by sandromosca on 27/12/15.
  */
case class User(nickname: String, server: String, port: Int, token: UUID)

trait UsersInterface { // TODO: Move everything to futures
  val name: String

  def getById(id: String): Option[User]

  def pairById(id: String): Option[(User, SessionImpl)]

  def getAll: Seq[User]

  def getSessionById(id: String): Option[SessionImpl]

  def add(request: ConnectionRequest): (User, SessionImpl)
}

