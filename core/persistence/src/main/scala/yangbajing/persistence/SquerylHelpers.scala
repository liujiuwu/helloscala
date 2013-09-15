package yangbajing.persistence

import java.sql.{ DriverManager, Date, Timestamp, Connection }

import org.squeryl.adapters.{ PostgreSqlAdapter, H2Adapter, MySQLInnoDBAdapter, MySQLAdapter, OracleAdapter }
import org.squeryl.{ Session, SessionFactory }

object SquerylHelpers {
  def createSession(sqlAdapter: String, dbUrl: String, dbUsername: String, dbPassword: String): Session =
    createSession(sqlAdapter, () => DriverManager.getConnection(dbUrl, dbUsername, dbPassword))

  def createSession(sqlAdapter: String, coll: () => Connection): Session = {
    val adapter =
      sqlAdapter.toLowerCase() match {
        case "postgres" =>
          JdbcHelpers.registerPostgresql
          new PostgreSqlAdapter
        case "mysqlinnodb" =>
          JdbcHelpers.registerMySQL
          new MySQLInnoDBAdapter
        case "mysql" =>
          JdbcHelpers.registerMySQL
          new MySQLAdapter
        case "oracle" =>
          JdbcHelpers.registerOracle
          new OracleAdapter
        case "h2" =>
          JdbcHelpers.registerH2
          new H2Adapter
        case _ =>
          JdbcHelpers.registerH2
          new H2Adapter
      }
    Session.create(coll(), adapter)
  }

  def createSessionFactory(sqlAdapter: String, dbUrl: String, dbUsername: String, dbPassword: String) =
    new SessionFactory {
      def newSession = createSession(sqlAdapter, dbUrl, dbUsername, dbPassword)
    }

}
