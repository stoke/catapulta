package com.catapulta.services

import java.util.UUID

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
  * Created by sandromosca on 27/12/15.
  */
object TablesService {
  def service: Service[Request, Response] = (showTables :+: joinTable :+: pollingTest).toService

  val showTables: Endpoint[Seq[Table]] = get("tables" ? tokenRequestReader) { (session: Session) =>
    FuturePool.unboundedPool {
      val mageSession = session.session
      implicit val mainRoom = mageSession.getMainRoomId

      val tables = mageSession.getTables(mainRoom).toSeq.map(tableViewToTable)

      Ok(tables)
    }
  }

  val joinTable: Endpoint[Table] = post("tables" / uuid("tableId") ? tokenRequestReader ? deckReader) {
    (tableId: UUID, session: Session, deck: DeckCardLists) =>

      FuturePool.unboundedPool {
        val mageSession = session.session
        implicit val mainRoom = mageSession.getMainRoomId

        mageSession.joinTable(mainRoom, tableId, session.user.nickname, "Human", 9, deck, "")

        val table = tableViewToTable( mageSession.getTable(mainRoom, tableId) )

        Ok(table)
      }
  }

  val pollingTest: Endpoint[String] = get("tables" / "poll") {
    FuturePool.unboundedPool {
      Thread.sleep(10000)

      Ok("polled")
    }
  }

}
