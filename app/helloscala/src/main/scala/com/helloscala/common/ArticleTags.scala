package com.helloscala.common

case class ArticleTags(id: Long, newTags: List[String], oldTags: List[String] = Nil)
