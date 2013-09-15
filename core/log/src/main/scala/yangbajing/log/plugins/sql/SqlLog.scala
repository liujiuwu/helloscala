package yangbajing.log
package plugins.sql

import org.joda.time.DateTime
import org.bson.types.ObjectId
import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column
import org.squeryl.Schema
import yangbajing.persistence.SquerylEntrypoint._

object SqlLog extends Schema {
  val logErrors = table[LogSql]("log_error_")
  on(logErrors)(t =>
    declare(
      t.e is (dbType("text"))))

  val logWarns = table[LogSql]("log_warn_")
  on(logWarns)(t =>
    declare(
      t.e is (dbType("text"))))

  val logImportants = table[LogSql]("log_important_")
  on(logImportants)(t =>
    declare(
      t.e is (dbType("text"))))

  val logInfos = table[LogSql]("log_info_")
  on(logInfos)(t =>
    declare(
      t.e is (dbType("text"))))

  val logDebugs = table[LogSql]("log_debug_")
  on(logDebugs)(t =>
    declare(
      t.e is (dbType("text"))))

  val logTraces = table[LogSql]("log_traces_")
  on(logTraces)(t =>
    declare(
      t.e is (dbType("text"))))

}

class LogSql(
  @Column("c_") val c: String, // 产生日志的类
  @Column("t_") val t: String, // 产生日志时所在线程
  @Column("m_") val m: String, // 日志内容
  @Column("e_") val e: Option[String], // 日志产生时的异常
  @Column("d_") val d: DateTime,
  @Column("id_") val id: String = ObjectId.get.toString) extends KeyedEntity[String]

