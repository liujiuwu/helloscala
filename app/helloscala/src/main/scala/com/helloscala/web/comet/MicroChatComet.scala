package com.helloscala.web
package comet

import scala.language.postfixOps

import net.liftweb.common.Loggable
import net.liftweb.http.{CometListener, CometActor}
import net.liftweb.http.js.JE

import com.helloscala.helper.MicroChat
import com.helloscala.web.MicroChatHelpers
import com.helloscala.service.MicroChatManager

class MicroChatComet extends CometActor with CometListener with Loggable {
  // 一个Session会话生成一个实例
  logger.debug(toString)

  def render = {
    val mcs = MicroChatManager.microChats
    "#micro-chat-list" #> ("li" #> mcs.map(MicroChatHelpers.chatLi(_)))
  }

  override def lowPriority = {
    case mc: MicroChat =>
      val node = MicroChatHelpers.chatLi(mc)
      val cmd =
        $("#micro-chat-list").prepared(node).cmd &
          JE.Call("listLiClear", "#micro-chat-list li", 5).cmd
      partialUpdate(cmd)

    case v =>
      logger.error("v: " + v)
  }

  protected def registerWith = MicroChatManager
}
