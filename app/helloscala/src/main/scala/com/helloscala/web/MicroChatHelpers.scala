package com.helloscala.web

import scala.xml.NodeSeq

import net.liftweb.http.Templates
import net.liftweb.util.Helpers._

import com.helloscala.helper.MicroChat
import com.helloscala.model.MUser

object MicroChatHelpers {

  def chatLi(mc: MicroChat): NodeSeq = {
    val cssSel = for (
      user <- MUser.find(mc.chat.creator)
    ) yield {
      val userHref = s"/u/${user.id}/"

      ".portrait *" #> mc.portrait &
        "@account [href]" #> userHref &
        "@account *" #> user.nick.getOrElse(user.id) &
        "@content *" #> mc.chat.content &
        "@time *" #> s"${mc.minutes}分钟前" &
        "@reply" #> mc.reply
    }

    cssSel.map(_.apply(nodeChatLi)) openOr NodeSeq.Empty
  }

  def nodeChatLi = Templates(List("page", "micro_chat", "_micro_chat_li")).openOrThrowException("/page/micro_chat/_micro_chat_li not found!!!")
}
