package com.catapulta.services

import java.util.UUID

import com.catapulta.EventManager
import com.catapulta.events.StartGame
import com.catapulta.messages._
import mage.cards.decks.DeckCardLists
import mage.view.TableView
import scala.collection.JavaConversions._
import com.catapulta.implicits._
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Response, Request}
import com.catapulta.interfaces.UsersMemory
import com.twitter.util.FuturePool

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._

/**
  * Created by sandromosca on 06/01/16.
  */
object GamesService {
  def service: Service[Request, Response] = (pollingGames).toService

  val pollingGames: Endpoint[StartGame] = get("games" ? tokenRequestReader) {
    (session: Session) =>
      implicit val user = session.user

      EventManager.future[StartGame]().map(Ok)
  }
}

