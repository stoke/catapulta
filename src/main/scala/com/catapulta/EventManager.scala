package com.catapulta

import java.util.concurrent._

import com.catapulta.models.User
import com.twitter.util.{Future, Promise}

import scala.collection.concurrent
import scala.collection.mutable
import scala.collection.JavaConversions._

/**
  * I hoped there was another way to do this but i don't think there is.
  *
  *
  * This is a singleton object that manages Event to be polled. Every case class can be a message if it extends
  * the "Event" trait, and everything is dispatched automatically around the _type_ of the message.
  *
  * Main APIs are:
  *
  * EventManager.future[T]:
  *
  * This returns a Future of type T.
  *
  * EventManager.add(...); e.g.: EventManager.add(StartGame(...)):
  *
  * This will just push an event to the listeners.
  *
  * Everything is (or should be?) thread safe using concurrent data structures
  *
  * NOTE: I know, this uses reflection. But it really is (I think) the only way to achieve such api
  * based around the _type_ of the message.
  *
  * The use of reflection allows for a REALLY fast inclusion of new messages and speeds-up development a lot
  * whereas using implicits or some other technique just doesn't cut it.
  *
  * Using reflection also means that we can allow for ANY case class to be used as a message without losing
  * static typing while consuming this API.
  *
  * If someone comes up with another solution i'm all ears.
  */


object events {
  sealed trait Event

  case class StartGame(id: String) extends Event
  case class TestEvent(n: Int) extends Event
}

object EventManager {
  import events._

  type EventBuffer = ConcurrentLinkedQueue[Event]
  type Callback = (Event => Boolean, Promise[Event])
  type CallbackBuffer = ConcurrentLinkedQueue[Callback]
  type CallbacksMap = concurrent.TrieMap[Manifest[_], CallbackBuffer]

  private implicit def callbackToPromise[T](x: Callback): Promise[Event] = x._2
  private implicit def promiseToCallback(x: Promise[Event])(implicit f: (Event => Boolean)): Callback = (f, x)

  case class EventUser(user: User, events: EventBuffer, callbacks: CallbacksMap)

  private val runnable = new Runnable {
    override def run() = dispatchEvents()
  }

  val executor = Executors.newScheduledThreadPool(1)

  executor.scheduleAtFixedRate(runnable, 0, 100, TimeUnit.MILLISECONDS) // is 100 ms low enough?

  private val users: java.util.concurrent.ConcurrentLinkedQueue[EventUser] =
    new java.util.concurrent.ConcurrentLinkedQueue[EventUser]()

  private def eventImplToEvent[T <: Event](value: T): Event = {
    value.asInstanceOf[Event]
  }

  private def eventToEventImpl[T <: Event](value: Event): T = {
    value.asInstanceOf[T]
  }

  private def addUser(user: User): EventUser = {
    val eventUser = EventUser(
      user,
      new ConcurrentLinkedQueue,
      concurrent.TrieMap.empty
    )

    users.add(eventUser)

    eventUser
  }

  private def lookupUser(user: User): Option[EventUser] = { // TODO: Merge this with addUser? I think it's better to divide them tho
    users.find({
      case EventUser(u, _, _) => u == user
    })
  }

  def dispatchEvents() = {
    for (
      user <- users;
      event <- user.events.iterator;
      manifest = Manifest.classType(event.getClass);
      callbacks <- user.callbacks.get(manifest).iterator;
      callback <- callbacks if callback._1(event)
    ) {
      println(s"Dispatching $event to $callback")

      dispatchEvent(callback, event)

      user.events.remove(event)

      user.callbacks -= manifest
    }
  }

  private def dispatchEvent[T <: Event](callback: Promise[Event], message: T) = {
    if (!callback.isDefined)
      callback.setValue(message)
  }

  def add(event: Event)(implicit user: User) = {
    val currentUser = lookupUser(user).getOrElse { addUser(user) }

    currentUser.events.add(event)
  }

  def +=(event: Event)(implicit user: User) = {
    add(event)
  }

  def future[T <: Event](filter: (T => Boolean) = { _: Any => true })
                        (implicit user: User, m: Manifest[T]): Future[T] = {
    val promise = Promise[Event]()
    val currentUser = lookupUser(user) getOrElse { addUser(user) }

    // This basically wraps a function that only accepts T into one that accepts
    // any Event, downcasting it. This is absolutely type safe because the event type is checked via manifest
    val polyFilterWrapper: PartialFunction[Event, Boolean] = {
      case event =>
        filter(event.asInstanceOf[T])
    }

    val callback: Callback = (polyFilterWrapper, promise)

    currentUser.callbacks.get(m) match {
      case Some(callbacks) =>
        callbacks add callback

      case None =>
        currentUser.callbacks += m -> new ConcurrentLinkedQueue[Callback]

        currentUser.callbacks(m) add callback
    }

    promise.map(eventToEventImpl[T])
  }
}
