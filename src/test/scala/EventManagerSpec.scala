import java.util.UUID

import com.catapulta.events.{TestEvent, StartGame}
import com.catapulta.models.User
import com.twitter.util.{Await, Future}
import org.specs2.mutable.Specification

import com.catapulta.EventManager

/**
  * Created by sandromosca on 06/01/16.
  */
class EventManagerSpec extends Specification {
  implicit val user = User("test", "test", 80, UUID.randomUUID)

  "EventManager" >> {
    "#future[T] should return a Future[T]" >> {
      EventManager.future[StartGame] must beAnInstanceOf[Future[StartGame]]
    }

    "#future[T] should return the same Future if called multiple times" >> {
      val f1 = EventManager.future[StartGame]
      val f2 = EventManager.future[StartGame]

      f1 must be(f2)
    }

    "#future[T] should get resolved if someone pushes a message through #add" >> {
      val t = TestEvent(100)
      val f = EventManager.future[TestEvent]

      EventManager add t

      // We can't do this because twitter Futures. Thanks twitter!
      //f must be_==(t).await

      Await.result(f) must be(t)
    }

    "#future[T] after one is resolved should return a new future" >> {
      val t = TestEvent(200)
      val f1 = EventManager.future[TestEvent]
      val f2 = EventManager.future[TestEvent]

      f1 must be(f2)

      EventManager add t

      Await.result(f1) must be(t)
      Await.result(f2) must be(t)

      val f3 = EventManager.future[TestEvent]

      f3 mustNotEqual f1
      f3 mustNotEqual f2
    }
  }

}
