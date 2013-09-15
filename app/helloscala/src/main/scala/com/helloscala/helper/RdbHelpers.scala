package com.helloscala.helper

import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.internals.DatabaseAdapter
import java.sql.{DriverManager, Connection}

object RdbHelpers {

  Class.forName("org.postgresql.Driver")

  def registerSquerylSession() {
    SessionFactory.concreteFactory =
      Some(() => session("jdbc:postgresql://localhost:5432/helloscala", "devuser", "devpass", new PostgreSqlAdapter))
  }

  @inline
  def session(url: String, username: String, password: String, adapter: DatabaseAdapter): Session =
    Session.create(getConnection(url, username, password), adapter)

  @inline
  def getConnection(url: String, username: String, password: String): Connection =
    DriverManager.getConnection(url, username, password)

}
