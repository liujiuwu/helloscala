package com.helloscala.web

import scala.xml.{Elem, NodeSeq}

import net.liftweb.http.Templates
import net.liftweb.util.Helpers._

import com.helloscala.helper.{HelloHelpers, MicroChat}
import com.helloscala.model.{MMicroChat, MUser}

object MicroChatHelpers {

  def chatLi(mc: MicroChat): NodeSeq = {
    val cssSel = for (
      user <- MUser.findOne(mc.chat.creator)
    ) yield {
      val userHref = W.hrefAccount(user.id)

      ".portrait *" #> mc.portrait &
        "@account [href]" #> userHref &
        "@account *" #> (user.nick.getOrElse(user.id) + "：") &
        "@content *" #> mc.chat.content &
        "@time *" #> HelloHelpers.timeDescription(mc.chat.createdAt) &
        "@reply" #> mc.reply
    }

    cssSel.map(_.apply(nodeChatLi)) openOr NodeSeq.Empty
  }

  def portrait(mc: MMicroChat): Elem = {
    //      <img src={s"/upload/user/${mc.creator}/user-32.png"}/>

    <a href={W.hrefAccount(mc.creator)}>
      <img src="/assets/img/logo-1-32.png"/>
    </a>
  }

  def nodeChatLi = Templates(List("page", "micro_chat", "_micro_chat_li")).openOrThrowException("/page/micro_chat/_micro_chat_li not found!!!")
}
