package yangbajing.persistence

import scala.util.Try

import java.sql.{Connection, SQLException, Timestamp}

import yangbajing.util.Imports._
import yangbajing.util.data.DataGenre
import yangbajing.common.A

object JdbcHelpers extends JdbcHelpers {

  def toTimestamp(tt: String): Timestamp =
    new Timestamp(Y.formaterDatetime.parse(tt).getTime)

  lazy val registerPostgresql = Class.forName("org.postgresql.Driver")

  lazy val registerMySQL = Class.forName("")

  lazy val registerOracle = Class.forName("")

  lazy val registerH2 = Class.forName("")
}

trait JdbcSession {
  def getConnection: Connection
}

trait JdbcHelpers {
  def query[R](
                columns: Seq[(String, DataGenre.Value)],
                tableName: String,
                func: Connection => R,
                where: Seq[(String, Any)] = Nil,
                orderBy: String = "",
                limit: Int = 0,
                offset: Int = 0)(implicit jdbcSession: JdbcSession): List[Map[String, Any]] =
    query(jdbcSession.getConnection) {
      conn =>
        val sql =
          columns.map {
            column =>
          }.mkString("select ", ", ", s" from ${tableName} ") +
            where.map(entry => entry._1).mkString(" where ", " and ", "") +
            orderBy + (if (limit > 0) " limit " + limit else "") + (if (offset > 0) " offset " + offset else "")

        val pstmt = conn.prepareStatement(sql)
        var idx = 1
        for ((_, value) <- where) {
          pstmt.setObject(idx, value)
          idx += 1
        }

        val buffer = collection.mutable.ListBuffer[Map[String, Any]]()
        val rs = pstmt.executeQuery()
        while (rs.next)
          buffer +=
            columns.map {
              case (label, genre) =>
                label -> (genre match {
                  case DataGenre.TEXT =>
                    rs.getString(label)
                  case DataGenre.DATETIME =>
                    rs.getTimestamp(label)
                  case DataGenre.INT =>
                    rs.getInt(label)
                  case DataGenre.LONG =>
                    rs.getLong(label)
                  case DataGenre.NUMBER =>
                    rs.getBigDecimal(label)
                  case DataGenre.DATE =>
                    rs.getDate(label)
                  case DataGenre.TIME =>
                    rs.getTime(label)
                  case _ =>
                    rs.getObject(label)
                })
            }.toMap

        buffer.toList
    } getOrElse Nil

  def query[R](func: Connection => R)(implicit jdbcSession: JdbcSession): Try[R] =
    query(jdbcSession.getConnection)(func)

  def query[R](_conn: => Connection)(func: Connection => R): Try[R] = Try {
    val conn = _conn
    try {
      func(conn)
    } catch {
      case e: SQLException =>
        e.printStackTrace()
        throw e
    } finally {
      if (conn ne null)
        try (conn.close()) catch {
          case _: Throwable =>
        }
    }
  }

  def execute(sql: String)(implicit jdbcSession: JdbcSession): Try[Boolean] =
    execute(jdbcSession.getConnection) {
      conn =>
        val stmt = conn.createStatement()
        stmt.execute(sql)
    }

  def execute[R](func: Connection => R)(implicit jdbcSession: JdbcSession): Try[R] =
    execute(jdbcSession.getConnection)(func)

  def execute[R](_conn: => Connection)(func: Connection => R): Try[R] = Try {
    val conn = _conn
    val oldAutoCommit = conn.getAutoCommit

    try {
      conn.setAutoCommit(false)
      val result = func(conn)
      conn.commit()
      result
    } catch {
      case e: SQLException =>
        conn.rollback()
        throw e
    } finally {
      if (conn ne null)
        try {
          conn.setAutoCommit(oldAutoCommit)
          conn.close()
        } catch {
          case _: Throwable =>
        }
    }
  }

  //  def execute[R](conn: Connection)(func: => R): Either[SQLException, R] = {
  //    try {
  //      conn.setAutoCommit(false)
  //      val result = func
  //      conn.commit()
  //      Right(result)
  //    } catch {
  //      case e: SQLException =>
  //        conn.rollback()
  //        Left(e)
  //    } finally {
  //      conn.setAutoCommit(true)
  //      conn.close()
  //    }
  //  }

}
