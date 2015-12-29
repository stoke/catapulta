import com.twitter.finagle.http.Request
import com.twitter.finagle.http.path.Root
import com.twitter.finagle.http.service.RoutingService
import mage._
import com.twitter.finagle.{Path, Http}
import com.twitter.util.Await
import com.catapulta.services.{TablesService, ConnectionService}
import com.catapulta.implicits._



/**
  * Created by sandromosca on 20/12/15.
  */
object Service extends App {
  val router = RoutingService.byPathObject[Request] {
    case x if x.startsWith(Root / "connection") => ConnectionService
    case x if x.startsWith(Root / "tables") => TablesService
  }

  Await.ready(Http.server.serve(":8080", router))
}
