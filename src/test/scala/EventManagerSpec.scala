import java.util.UUID

import com.catapulta.events.{TestEvent, StartGame}
import com.catapulta.models.User
import com.twitter.util.{Await, Future, Duration}
import org.specs2.mutable.Specification

import com.catapulta.EventManager

/**
  * Created by sandromosca on 06/01/16.
  */
class EventManagerSpec extends Specification {
  sequential

  implicit val user = User("test", "test", 80, UUID.randomUUID)

  val seconds = Duration.fromSeconds(5)

  "EventManager" >> {
    "#future[T] should return a Future[T]" >> {
      EventManager.future[StartGame]() must beAnInstanceOf[Future[StartGame]]
    }

    "#future[T] should return the same Future if called multiple times" >> {
      val f1 = EventManager.future[StartGame]()
      val f2 = EventManager.future[StartGame]()

      EventManager add StartGame("testId")

      Await.result(f1, seconds) must be(Await.result(f2, seconds))
    }

    "#future[T] should get resolved if someone pushes a message through #add" >> {
      val t = TestEvent(100)
      val f = EventManager.future[TestEvent]()

      EventManager add t

      // We can't do this because twitter Futures. Thanks twitter!
      //f must be_==(t).await

      Await.result(f, seconds) must be(t)
    }

    "#future[T] after one is resolved should return a new future" >> {
      val t = TestEvent(200)

      val f1 = EventManager.future[TestEvent]()
      val f2 = EventManager.future[TestEvent]()

      EventManager add t

      Await.result(f1, seconds) must be(t)
      Await.result(f2, seconds) must be(t)

      val f3 = EventManager.future[TestEvent]()

      val t1 = TestEvent(100)

      EventManager add t1

      Await.result(f3, seconds) mustNotEqual Await.result(f1, seconds)
      Await.result(f3, seconds) mustNotEqual Await.result(f2, seconds)
      Await.result(f3, seconds) must be(t1)
    }

    "#future[T](fn) should resolve only if fn returns true on event" >> {
      val t = TestEvent(100)

      val f1 = EventManager.future[TestEvent]( _.n == 100 )
      val f2 = EventManager.future[TestEvent]( _.n == 200 )

      EventManager add t

      Await.result(f1, seconds) must be(t)
      Await.result(f2, seconds) must throwAn()
    }
  }

}
