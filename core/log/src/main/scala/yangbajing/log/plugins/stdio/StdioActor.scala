package yangbajing.log
package plugins.stdio

import akka.actor.Actor

import yangbajing.log.LogTypes.Level._

private[log] class StdioActor(val levels: Seq[Level]) extends Actor {
  private var out: java.io.PrintStream = null

  def receive = {
    case log: Log if out ne null =>
      out.println(log)

    case LoggerStop =>
      context stop self

    case o: java.io.OutputStream =>
      out = new java.io.PrintStream(o)
  }

  override def preStart() {
    LoggerSystem.is ! LoggerSubscribe(self, levels)
    println("%s start levels %s" format(self, levels))
  }

  override def postStop() {
    println("%s stop levels %s" format(self, levels))
    LoggerSystem.is ! LoggerUnsbuscribe(self, levels)
  }

}

