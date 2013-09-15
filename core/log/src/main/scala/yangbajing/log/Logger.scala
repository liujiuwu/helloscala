package yangbajing.log

import org.joda.time.DateTime

import scala.util.Try

import yangbajing.persistence.SquerylHelpers
import yangbajing.util.Imports._

import LogTypes.Level._
import org.squeryl.{Session, SessionFactory}

object Logger {
  def apply(clazz: Class[_]): Logger = apply(clazz.getName)

  def apply(className: String): Logger = {
    val be = isRunning
    new DefaultLogger(className, be)
  }

  object rules extends LoggerRules

  def isRunning = plugins.stdioActor.ne(null) || plugins.sqlActor.ne(null) || plugins.mongoActor.ne(null)

  def start() {
    if (isRunning) LoggerSystem.is ! "" // 启动LoggerSystem
  }

  def stop() {
    if (isRunning) {
      plugins.sqlStop()
      plugins.mongodbStop()
      plugins.stdioStop()
      LoggerSystem.system.shutdown()
    }
  }

  object plugins {

    import akka.actor.{Props, ActorRef}
    import yangbajing.log.plugins.mongodb.{MongoActor, MongoConnUri, MongoLog}
    import yangbajing.log.plugins.stdio.StdioActor
    import yangbajing.log.plugins.sql.{SqlActor, SqlLog}

    private[Logger] var stdioActor: ActorRef = null

    def stdioRun(out: java.io.OutputStream, levels: Seq[Level] = Seq()) {
      stdioActor = LoggerSystem.system.actorOf(Props(new StdioActor(levels)), "ybj-logger-plugin-stdio")
      stdioActor ! out
    }

    private[Logger] def stdioStop() {
      if (stdioActor ne null)
        stdioActor ! LoggerStop
    }

    private[Logger] var sqlActor: ActorRef = null

    def sqlRun(sqlAdapter: String, dbUrl: String, username: String, password: String, levels: Seq[Level] = Seq()) {
      sqlActor = LoggerSystem.system.actorOf(Props(new SqlActor(levels)), "ybj-logger-plugin-sql")
      sqlActor ! new SessionFactory {
        def newSession: Session = SquerylHelpers.createSession(sqlAdapter, dbUrl, username, password)
      }
    }

    private[Logger] def sqlStop() {
      if (sqlActor ne null)
        sqlActor ! LoggerStop
    }

    private[Logger] var mongoActor: ActorRef = null

    def mongodbRun(host: String, port: Int = 27017, db: String = "ybj_log", collection: String = "app_log", username: Option[String] = None, password: Option[String] = None, levels: Seq[Level] = Seq()) {
      mongoActor = LoggerSystem.system.actorOf(Props(new MongoActor(levels)), "ybj-logger-plugin-mongodb")
      mongoActor ! MongoConnUri(host, port, db, collection, username, password)
    }

    private[Logger] def mongodbStop() {
      if (mongoActor ne null)
        mongoActor ! LoggerStop
    }

  }

  object empty extends Logger {
    val className = "Logger.empty"

    def logging[T <: AnyRef](level: Level, clazz: String, msg: => T, t: => Option[Throwable]) {}
  }

}

trait Logger {
  def className: String

  def logging[T <: AnyRef](level: Level, clazz: String, msg: => T, t: => Option[Throwable])

  def error(msg: => AnyRef, t: Throwable) {
    logging(ERROR, className, msg, Some(t))
  }

  def error(msg: => AnyRef, t: Option[Throwable]) {
    logging(ERROR, className, msg, t)
  }

  def error(msg: => AnyRef) {
    logging(ERROR, className, msg, None)
  }

  def warn(msg: => AnyRef, t: Throwable) {
    logging(WARN, className, msg, Some(t))
  }

  def warn(msg: => AnyRef, t: Option[Throwable]) {
    logging(WARN, className, msg, t)
  }

  def warn(msg: => AnyRef) {
    logging(WARN, className, msg, None)
  }

  def important(msg: => AnyRef, t: Throwable) {
    logging(IMPORTANT, className, msg, Some(t))
  }

  def important(msg: => AnyRef, t: Option[Throwable]) {
    logging(IMPORTANT, className, msg, t)
  }

  def important(msg: => AnyRef) {
    logging(IMPORTANT, className, msg, None)
  }

  def info(msg: => AnyRef, t: Throwable) {
    logging(INFO, className, msg, Some(t))
  }

  def info(msg: => AnyRef, t: Option[Throwable]) {
    logging(INFO, className, msg, t)
  }

  def info(msg: => AnyRef) {
    logging(INFO, className, msg, None)
  }

  def debug(msg: => AnyRef, t: Throwable) {
    logging(DEBUG, className, msg, Some(t))
  }

  def debug(msg: => AnyRef, t: Option[Throwable]) {
    logging(DEBUG, className, msg, t)
  }

  def debug(msg: => AnyRef) {
    logging(DEBUG, className, msg, None)
  }

  def trace(msg: => AnyRef, t: Throwable) {
    logging(TRACE, className, msg, Some(t))
  }

  def trace(msg: => AnyRef, t: Option[Throwable]) {
    logging(TRACE, className, msg, t)
  }

  def trace(msg: => AnyRef) {
    logging(TRACE, className, msg, None)
  }

}

class DefaultLogger(val className: String, be: Boolean) extends Logger {

  import Logger.rules._

  def logging[T <: AnyRef](level: Level, clazz: String, msg: => T, e: => Option[Throwable]) {
    def sendLog {
      val log = Log(
        l = level,
        c = if (clazz eq null) "" else clazz,
        t = Thread.currentThread.getName,
        m = Try(msg.toString).getOrElse(""),
        e = e)

      LoggerSystem.is ! log
    }

    if (be)
      level match {
        case ERROR /*if enableError*/ => sendLog
        case WARN /*if enableWarn*/ => sendLog
        case IMPORTANT if enableImportant => sendLog
        case INFO if enableInfo => sendLog
        case DEBUG if enableDebug => sendLog
        case TRACE if enableTrace => sendLog
        case _ =>
      }
    else
      println(be)
  }

}

