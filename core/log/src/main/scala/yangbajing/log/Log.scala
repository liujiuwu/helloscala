package yangbajing.log

import java.io._

import org.joda.time.DateTime

import LogTypes.Level._

import yangbajing.util.Imports._

case class Log(
  l: Level, // 日志级别
  c: String, // 产生日志的类
  t: String, // 产生日志时所在线程
  m: String, // 日志内容
  e: Option[Throwable], // 日志产生时的异常
  d: DateTime = DateTime.now) {

  override def toString = {
    //    "%s level:[%s] class:[%s] thread:[%s], except:[%s], key:[%s], msg:\n%s" format (d.formatTimeMillis, l, c, t, e, k, m)
    val sw = new StringWriter()
    val pw = new PrintWriter(sw)
    for (v <- e) v.printStackTrace(pw)

    sw write "\n%s level:[%s] class:[%s] thread:[%s], msg:\n%s\n".format(Y.formatDateTime print d, l, c, m)
    val ret = sw.toString
    pw.close

    ret
  }

  // 方便casbah 的Map -> MongoDbObject 隐式转换
  def toMap: Map[String, AnyRef] = {
    val map = scala.collection.mutable.Map[String, AnyRef]("l" -> l.toString, "c" -> c, "t" -> t, "m" -> m, "d" -> d)
    e.foreach(map.put("e", _))
    map.toMap
  }
}

trait LoggerRules {
  @volatile var enableError = true
  @volatile var enableWarn = true
  @volatile var enableImportant = true
  @volatile var enableInfo = true
  @volatile var enableDebug = true
  @volatile var enableTrace = false

  lazy val enableRules: Seq[Level] = Seq(
    if (enableError) Some(ERROR) else None,
    if (enableWarn) Some(WARN) else None,
    if (enableImportant) Some(IMPORTANT) else None,
    if (enableInfo) Some(INFO) else None,
    if (enableDebug) Some(DEBUG) else None,
    if (enableTrace) Some(TRACE) else None).flatten

}
