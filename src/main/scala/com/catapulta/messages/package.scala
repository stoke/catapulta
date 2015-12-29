package com.catapulta

import java.util.UUID

import com.catapulta.interfaces.UsersMemory
import com.catapulta.models.User
import com.twitter.util.Future
import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import mage.cards.decks.DeckCardLists
import mage.constants.TableState
import mage.remote.SessionImpl
import com.catapulta.implicits._

/**
  * Created by sandromosca on 27/12/15.
  */
package object messages {
  case class ConnectionRequest(server: String, port: Int, nickname: String)
  case class Token(token: UUID)
  case class Table(id: UUID, name: String, gameType: String, tableState: TableState, roomId: UUID, games: Seq[UUID])
  case class Card(cardName: String, setCode: String, cardNumber: Int, quantity: Int)
  case class Deck(mainboard: Seq[Card], sideboard: Seq[Card])

  case class Session(user: User, session: SessionImpl) // TODO: move away or rename the package

  case class UserNotFound(id: String) extends Error {
    override def getMessage = s"User $id not found"
  }

  val connectionRequestReader: RequestReader[ConnectionRequest] =
    (
      param("server")       ::
      param("port").as[Int] ::
      param("nickname")
    ).as[ConnectionRequest]

  val tokenRequestReader: RequestReader[Session] =
    header("X-Catapulta-Token").embedFlatMap(id => UsersMemory.pairById(id) match {
      case Some(pair) => Future.value( Session.tupled(pair) )
      case None => Future.exception( UserNotFound(id) )
    })

  val deckReader: RequestReader[DeckCardLists] = body.as[Deck].map(x => { println(x); deckToDeckCardLists(x) })


}
