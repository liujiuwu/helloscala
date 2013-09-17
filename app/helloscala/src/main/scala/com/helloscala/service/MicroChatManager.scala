package com.helloscala.service

import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager

import com.helloscala.helper.MicroChat
import net.liftweb.common.Loggable
import com.helloscala.model.MMicroChat

object MicroChatManager extends LiftActor with ListenerManager with Loggable {
  var microChats: List[MicroChat] = MMicroChat.findAllLast(5).map(v => MicroChat(v))

  override protected def lowPriority = {
    case mc: MicroChat =>
      microChats = mc :: microChats.take(4)
      updateListeners(mc)

    case v =>
      logger.warn("v: " + v)
  }

  protected def createUpdate = microChats
}
