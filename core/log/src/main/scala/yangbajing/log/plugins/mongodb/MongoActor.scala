package yangbajing.log
package plugins.mongodb

import akka.actor.Actor

import LogTypes.Level._

private[log] class MongoActor(val levels: Seq[Level]) extends Actor {
  var mongo: MongoLog = null

  def receive = {
    case log: Log =>
      mongo += log

    case LoggerStop =>
      mongo.conn.close()
      context stop self

    case MongoConnUri(host, port, db, collection, username, password) =>
      mongo = new MongoLog(host, port, db, collection, username, password)

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

