package com.helloscala.model

import yangbajing.persistence.SquerylEntrypoint._
import org.squeryl.Schema

object Entities extends Schema {
  val users = table[MUser]("user_")
  on(users)(entity =>
    declare(
      entity.spaceName is(unique, dbType("varchar(32)"))
    )
  )

  val userPasswords = table[MUserPassword]("user_password_")

  val microChats = table[MMicroChat]("micro_chat_")

  val articles = table[MArticle]("article_")
  on(articles)(entity =>
    declare(
      entity.content is (dbType("text"))
    )
  )

  val comments = table[MComment]("comment_")
  on(comments)(entity =>
    declare(
      entity.content is (dbType("text"))
    )
  )


  val tags = table[MTag]("tag_")
  val articlesToTags =
    manyToManyRelation(articles, tags, "articles_tags_").via[MArticlesTags]((a, t, at) => (a.id === at.articleId, t.id === at.tagId))

  override def defaultLengthOfString: Int = 1024
}
