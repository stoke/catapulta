package com.catapulta

import java.util
import java.util.UUID
import mage.cards.repository.CardInfo

import scala.collection.JavaConversions._

import com.catapulta.interfaces.UsersMemory
import com.catapulta.messages.{Card, Deck, Table, Token}
import com.catapulta.models.User
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.io.Buf
import io.circe.Encoder
import mage.cards.decks.{DeckCardInfo, DeckCardLists}

import io.finch.{EncodeResponse, DecodeRequest, Error}
import mage.constants.TableState
import mage.remote.Connection

import com.twitter.util.Try
import mage.view.TableView

import io.circe.Json

/**
  * Created by sandromosca on 27/12/15.
  */
package object implicits {
  implicit def serviceImplToService[A <: { def service: Service[Request, Response] }](impl: A):
    Service[Request, Response] = impl.service

  implicit def tableViewToTable(t: TableView)(implicit roomId: UUID): Table = {
    Table(t.getTableId, t.getTableName, t.getGameType, t.getTableState, roomId, t.getGames.toSeq)
  }

  implicit def cardToCardInfo(card: Card): DeckCardInfo =
    new DeckCardInfo(card.cardName, card.cardNumber, card.setCode, card.quantity)

  implicit def deckToDeckCardLists(deck: Deck): DeckCardLists = {
    val lists = new DeckCardLists
    val mainboardCards = deck.mainboard.map(cardToCardInfo)
    val sideboardCards = deck.sideboard.map(cardToCardInfo)

    lists.setCards(new util.ArrayList ++ mainboardCards)
    lists.setSideboard(new util.ArrayList ++ sideboardCards)

    lists
  }

  implicit val tableStateEncoder: Encoder[TableState] = Encoder.instance[TableState](ts => Json.string(ts.toString))

}

package object utils {
  def connect(user: User): Connection = {
    val connection = new Connection()

    connection.setHost(user.server)
    connection.setPort(user.port)
    connection.setUsername(user.nickname)
    connection.setPort(user.port)
    connection.setProxyType(Connection.ProxyType.NONE)

    connection
  }
}
