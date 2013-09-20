package com.helloscala.model

import org.squeryl.KeyedEntity
import org.joda.time.DateTime

case class MComment(
                articleId: Long,
                creator: String,
                content: String,
                createdAt: DateTime = DateTime.now()) extends KeyedEntity[Long] {
  val id: Long = 0L
}
