package sample.squeryl.basic.helper

import org.squeryl.{Session, SessionFactory}
import java.sql.{DriverManager, Connection}
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.adapters.PostgreSqlAdapter

object RdbHelpers {
  Class.forName("org.postgresql.Driver")

  def registerSquerylSession() {
    SessionFactory.concreteFactory = Some(() => defaultSession)
  }

  @inline
  def defaultSession: Session =
    session("jdbc:postgresql://localhost:5432/devdb", "devuser", "devpass", new PostgreSqlAdapter)

  @inline
  def session(url: String, username: String, password: String, adapter: DatabaseAdapter): Session =
    Session.create(getConnection(url, username, password), adapter)

  @inline
  def getConnection(url: String, username: String, password: String): Connection =
    DriverManager.getConnection(url, username, password)
}
