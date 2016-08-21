/*
 * Copyright 2016 Sandro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import mage.cards.decks.importer.TxtDeckImporter
import mage.constants.{MatchTimeLimit, MultiplayerAttackOption, RangeOfInfluence}
import mage.game.`match`.MatchOptions
import mage.interfaces.MageClient
import mage.interfaces.callback.ClientCallback
import mage.remote.{Connection, SessionImpl}
import mage.utils.MageVersion
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import wrapper.CataMageVersion

import scala.collection.JavaConversions._

/**
  * Created by Sandro on 19/08/2016.
  */
trait CataClient extends MageClient {
    val log: Logger

    override def connected(s: String): Unit = log.info(s"Connected $s")
    override def disconnected(b: Boolean): Unit = log.info(s"Disconnected $b")
    override def showMessage(s: String): Unit = log.info(s"Message $s")

    override def showError(s: String): Unit = log.error(s"Error $s")

    override def processCallback(clientCallback: ClientCallback): Unit = log.info("ProcessCallback")

    override def getVersion: MageVersion = CataMageVersion.version
}

object Main extends App {
    val username = args.headOption getOrElse "TestUsername"

    val logger = LoggerFactory.getLogger(classOf[CataClient])

    val client = new CataClient {
        val log = logger
    }

    val session = new SessionImpl(client)

    val connection = new Connection()

    connection.setUsername(username)
    connection.setHost("localhost")
    connection.setPort(17171)
    connection.setProxyType(Connection.ProxyType.NONE)

    session.connect(connection)

    val roomId = session.getMainRoomId

    val tables = session.getTables(roomId).toSeq

    tables.foreach(table =>
        logger.info(s"Table player ${table.getControllerName}")
    )

    val gameTypes = session.getGameTypes.toSeq
    val gameType = gameTypes.head

    gameTypes.foreach(gameType =>
        logger.info(s"Game Type: ${gameType.getName}")
    )

    session.getDeckTypes.foreach(deckType =>
        logger.info(s"Deck type: $deckType")
    )

    val mo = new MatchOptions(gameType.getName, gameType.getName)

    mo.getPlayerTypes.add("Human")
    mo.getPlayerTypes.add("Human")

    mo.setDeckType(session.getDeckTypes.head)
    mo.setMatchTimeLimit(MatchTimeLimit.MIN__20)
    mo.setLimited(false)
    mo.setAttackOption(MultiplayerAttackOption.MULTIPLE)
    mo.setRange(RangeOfInfluence.ALL)
    mo.setWinsNeeded(1)

    session.getTables(roomId).foreach(table => {
        session.leaveTable(roomId, table.getTableId)
        session.removeTable(roomId, table.getTableId)
    })

    val table = session.createTable(roomId, mo)

    logger.info(s"Created table ${table.getTableName} with type ${table.getDeckType}")

    val importer = new TxtDeckImporter
    val deck = importer.importDeck("deck.txt")

    val joined = session.joinTable(roomId, table.getTableId, username, "Human", 1, deck, "")

    logger.info(s"Joined $joined")

    Thread.sleep(30000)

    session.disconnect(false)
}
