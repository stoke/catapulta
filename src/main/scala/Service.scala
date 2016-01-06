import java.util.UUID

import com.catapulta.models.User
import com.twitter.finagle.http.Request
import com.twitter.finagle.http.path.Root
import com.twitter.finagle.http.service.RoutingService
import mage._
import com.twitter.finagle.{Path, Http}
import com.twitter.util.Await
import com.catapulta.services.{GamesService, TablesService, ConnectionService}
import com.catapulta.implicits._

import com.catapulta.events._
import com.catapulta.EventManager


/**
  * Created by sandromosca on 20/12/15.
  */
object Service extends App {
  val router = RoutingService.byPathObject[Request] {
    case x if x.startsWith(Root / "connection") => ConnectionService
    case x if x.startsWith(Root / "tables") => TablesService
    case x if x.startsWith(Root / "games") => GamesService
  }

  Await.ready(Http.server.serve(":8080", router))
}
