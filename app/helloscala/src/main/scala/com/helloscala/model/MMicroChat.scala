package com.helloscala.model

import org.joda.time.DateTime
import org.squeryl.KeyedEntity

import net.liftweb.common.Box

case class MMicroChat(
                       creator: String,
                       content: String,
                       isMyChat: Boolean = true,
                       replayCount: Int = 0,
                       replyId: Option[Long] = None,
                       createdAt: DateTime = DateTime.now) extends KeyedEntity[Long] {
  val id: Long = 1L

  def this() {
    this("", "")
  }
}

import scala.language.postfixOps

import yangbajing.util.Imports.Y
import yangbajing.persistence.SquerylEntrypoint._

object MMicroChat {
  def findAllLast(len: Int): List[MMicroChat] =
    transaction(from(Entities.microChats)(m =>
      where(
        m.replyId isNull
      ) select (m) orderBy (m.createdAt desc)
    ).page(0, 5).toList)

  def incReplayCount(id: Long) {
    transaction(update(Entities.microChats)(m =>
      where(m.id === id) set (m.replayCount := m.replayCount + 1)
    ))
  }

  def create(creator: String, content: String, isMyChat: Boolean = true, replayId: Option[Long] = None): Box[MMicroChat] =
    Y.tryBox {
      val mc = MMicroChat(creator, content, isMyChat, 0, replayId)

      for (id <- replayId if id > 0) {
        incReplayCount(id)
      }

      transaction(Entities.microChats insert mc)
    }

  def findById(id: Long): Box[MMicroChat] =
    Y.tryBox {
      require(id > 0L, "id必需大于0L")

      transaction(from(Entities.microChats)(m =>
        where(m.id === id) select (m)
      ).single)
    }

  def findAllById(id: Long): Box[(MMicroChat, List[MMicroChat])] =
    for (
      m <- findById(id);
      ms <- findAll(replayId = Some(id))
    ) yield m -> ms

  def findAll(replayId: Option[Long] = None): Box[List[MMicroChat]] =
    Y.tryBox {
      replayId foreach (id => require(id > 0L, "replayId必需大于0L"))

      transaction(from(Entities.microChats)(m =>
        where(
          m.replyId === replayId.?
        ) select (m) orderBy (m.createdAt desc)
      ).toList)
    }

}
