package yangbajing.log

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props

import LogTypes.Level._

// levels is empty 订阅所有日志级别
case class LoggerSubscribe(sub: ActorRef, levels: Seq[Level] = Seq())

// levels is empty 取消订阅所有日志级别
case class LoggerUnsbuscribe(sub: ActorRef, levels: Seq[Level] = Seq())

case object LoggerSystemStop

case object LoggerStop

object LoggerSystem {
  val systemName = "ybj-logger-system"
  private[log] lazy val system = ActorSystem(systemName)

  private lazy val _main = system.actorOf(Props[LoggerSystem], "ybj-logger-system")

  def is = _main
}

private class LoggerSystem extends Actor {
  private var errors: Set[ActorRef] = Set()
  private var warnings: Set[ActorRef] = Set()
  private var importants: Set[ActorRef] = Set()
  private var infos: Set[ActorRef] = Set()
  private var debugs: Set[ActorRef] = Set()
  private var traces: Set[ActorRef] = Set()

  def receive = {
    case log: Log =>
      routerLog(log)

    case LoggerSubscribe(sub, levels) =>
      if (levels.isEmpty)
        allLevel.foreach(subscribe(sub, _))
      else
        levels.foreach(subscribe(sub, _))

    case LoggerUnsbuscribe(sub, levels) =>
      if (levels.isEmpty)
        allLevel.foreach(unsubscribe(sub, _))
      else
        levels.foreach(unsubscribe(sub, _))

    case LoggerSystemStop =>

  }

  private val allLevel = ERROR :: WARN :: IMPORTANT :: INFO :: DEBUG :: TRACE :: Nil

  private def routerLog(log: Log) = log.l match {
    case ERROR => errors.foreach(_ ! log)
    case WARN => warnings.foreach(_ ! log)
    case IMPORTANT => importants.foreach(_ ! log)
    case INFO => infos.foreach(_ ! log)
    case DEBUG => debugs.foreach(_ ! log)
    case TRACE => traces.foreach(_ ! log)
  }

  private def subscribe(sub: ActorRef, level: Level) = level match {
    case ERROR => errors += sub
    case WARN => warnings += sub
    case IMPORTANT => importants += sub
    case INFO => infos += sub
    case DEBUG => debugs += sub
    case TRACE => traces += sub
  }

  private def unsubscribe(sub: ActorRef, level: Level) = level match {
    case ERROR => errors -= sub
    case WARN => warnings -= sub
    case IMPORTANT => importants -= sub
    case INFO => infos -= sub
    case DEBUG => debugs -= sub
    case TRACE => traces -= sub
  }

  override def preStart() = {
    println(self + " start...")
  }

  override def postStop() = {
    println(self + " stop...")

    // TODO need?
    errors.foreach(_ ! LoggerStop)
    warnings.foreach(_ ! LoggerStop)
    importants.foreach(_ ! LoggerStop)
    infos.foreach(_ ! LoggerStop)
    debugs.foreach(_ ! LoggerStop)
    traces.foreach(_ ! LoggerStop)
  }
}
