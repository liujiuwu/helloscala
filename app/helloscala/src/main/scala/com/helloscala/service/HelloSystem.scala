package com.helloscala.service

import akka.actor.{Actor, Props, ActorSystem}

import com.helloscala.helper.HelloHelpers
import com.helloscala.model.MArticle
import com.helloscala.common.ArticleTags

object HelloSystem {
  val system = ActorSystem("hello-system")

  val tagActor = system.actorOf(Props[TagActor])
}

class TagActor extends Actor {
  def receive = {
    case ArticleTags(articleId, newTags, oldTags) =>
      setTags
      saveTags(newTags)
      MArticle.saveArticleTags(articleId, newTags, oldTags)

    case _ => // do nothing
  }

  private[this] def saveTags(tags: Seq[String]) {
    val newTags = tags diff HelloHelpers.tags
    MArticle.createTags(newTags)
  }

  private[this] def setTags: Unit =
    if (HelloHelpers.tags.isEmpty) HelloHelpers.tags = MArticle.findAllTags()

}
