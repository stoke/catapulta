import java.util.UUID

import com.catapulta.models.User
import com.twitter.finagle.http.Request
import com.twitter.finagle.http.path.Root
import com.twitter.finagle.http.service.RoutingService
import mage._
import com.twitter.finagle.{Path, Http}
import com.twitter.util.Await
import com.catapulta.services.{TablesService, ConnectionService}
import com.catapulta.implicits._

import com.catapulta.events._
import com.catapulta.EventManager


/**
  * Created by sandromosca on 20/12/15.
  */
object Service extends App {
  /*val router = RoutingService.byPathObject[Request] {
    case x if x.startsWith(Root / "connection") => ConnectionService
    case x if x.startsWith(Root / "tables") => TablesService
  }

  Await.ready(Http.server.serve(":8080", router))*/

  implicit val user = User("test", "test", 9090, UUID.randomUUID)
  val user1 = User("test1", "test", 9090, UUID.randomUUID)

  val f = EventManager.future[StartGame]
  val f1 = EventManager.future[StartGame](user1, manifest[StartGame])

  f onSuccess(m => println(s"It works $m"))
  f1 onSuccess(msg => println(s"It works on second user $msg"))

  EventManager.add(StartGame("testetst"))(user1)
  EventManager add StartGame("testetsta")

  EventManager.dispatchEvents()
}
