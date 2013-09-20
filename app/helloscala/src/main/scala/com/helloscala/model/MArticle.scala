package com.helloscala.model

import scala.language.postfixOps

import org.squeryl.KeyedEntity
import org.joda.time.DateTime

import net.liftweb.common.Box

import org.squeryl.dsl.CompositeKey2

import yangbajing.persistence.SquerylEntrypoint._
import yangbajing.util.Imports.Y
import yangbajing.util.{CompareCode, Pager}

object MArticle {
  def commentSize(articleId: Long): Long =
    transaction(from(Entities.comments)(c => where(c.articleId === articleId) compute (count(c.id))))

  def createComment(articleId: Long, creator: String, content: String): Box[MComment] =
    Y.tryBox {
      val c = MComment(articleId, creator, content)
      transaction(Entities.comments insert c)
    }

  def findAllTagsByArticle(articleId: Long): List[String] =
    transaction(from(Entities.articlesToTags)(at => where(at.articleId === articleId) select (at.tagId)).toList)

  def findAllTags(): List[String] =
    transaction(from(Entities.tags)(t => select(t.id)).toList)

  def createTags(tags: Seq[String]): Box[Unit] =
    Y.tryBox {
      require(tags.nonEmpty, "tags must non empty.")
      transaction(Entities.tags insert tags.map(MTag(_)))
    }

  def saveArticleTags(articleId: Long, newTags: List[String], oldTags: List[String] = Nil): Box[Unit] =
    Y.tryBox {
      Entities.articlesToTags deleteWhere (_.articleId === articleId)
      Entities.articlesToTags insert newTags.map(MArticlesTags(articleId, _))
    }

  def create(article: MArticle, tags: List[String] = Nil): Box[MArticle] =
    Y.tryBox(transaction(Entities.articles insert article))

  def save(article: MArticle): Box[MArticle] =
    Y.tryBox {
      transaction(Entities.articles update article)
      article
    }

  def pager(_limit: Int, _id: Option[Long], compare: CompareCode.Value) =
    new Pager[MArticle, Long] {
      val limit = _limit
      val id = _id
      val total = size()
      val page = transaction(
        from(Entities.articles)(a =>
          where(
            _compare(a)
          ) select (a) orderBy (a.createdAt desc)
        ).page(0, limit).toList)

      private[this] def _compare(a: MArticle) =
        compare match {
          case CompareCode.Gt => a.id gt id.?
          case CompareCode.Gte => a.id gte id.?
          case CompareCode.Lt => a.id lt id.?
          case CompareCode.Lte => a.id lte id.?
        }
    }

  def size(): Long =
    transaction(from(Entities.articles)(a =>
      compute(count(a.id))))

  def findOneById(id: Long): Box[MArticle] =
    Y.tryFlatBox {
      transaction(from(Entities.articles)(a => where(a.id === id) select (a)).singleOption)
    }

  def findAllComments(id: Long): List[MComment] =
    transaction(from(Entities.comments)(c => where(c.articleId === id) select (c) orderBy (c.createdAt desc)).toList)

}

case class MArticle(
                     author: String,
                     title: String,
                     content: String,

                     createdAt: DateTime = DateTime.now) extends KeyedEntity[Long] with M {


  val id: Long = 0L

  lazy val comments = MArticle.findAllComments(id)

  lazy val tags = MArticle.findAllTagsByArticle(id)
}

case class MTag(id: String) extends KeyedEntity[String]

case class MArticlesTags(articleId: Long, tagId: String) extends KeyedEntity[CompositeKey2[Long, String]] {
  def id = compositeKey(articleId, tagId)
}


