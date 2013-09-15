package yangbajing.log
package plugins.sql

import java.io.{StringWriter, PrintWriter}

import akka.actor.Actor

import org.squeryl.SessionFactory

import yangbajing.persistence.SquerylEntrypoint._

import LogTypes.Level._

private[log] class SqlActor(val levels: Seq[Level]) extends Actor {
  var factory: SessionFactory = null

  def receive = {
    case log: Log => // TODO 批处理写入数据库！
      val es =
        log.e.map {
          e =>
            val sw = new StringWriter
            val otw = new PrintWriter(sw)
            e.printStackTrace(otw)
            otw.flush
            val r = sw.toString
            otw.close()
            r
        }

      val t = new LogSql(log.c, log.t, log.m, es, log.d)

      transaction(factory)(log.l match {
        case ERROR =>
          SqlLog.logErrors insert t
        case WARN =>
          SqlLog.logWarns insert t
        case IMPORTANT =>
          SqlLog.logImportants insert t
        case INFO =>
          SqlLog.logInfos insert t
        case DEBUG =>
          SqlLog.logDebugs insert t
        case TRACE =>
          SqlLog.logTraces insert t
      })

    case LoggerStop =>
      LoggerSystem.is ! LoggerUnsbuscribe(self)
      context stop self

    case s: SessionFactory =>
      factory = s

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

