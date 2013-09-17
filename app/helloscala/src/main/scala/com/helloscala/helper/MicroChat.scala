package com.helloscala.helper

import scala.xml.Elem

import org.joda.time.{Minutes, DateTime}

import com.helloscala.model.MMicroChat
import com.helloscala.web.{MicroChatHelpers, W}

case class MicroChat(chat: MMicroChat) {
  val portrait = MicroChatHelpers.portrait(chat)

  val minutes: Int = Minutes.minutesBetween(chat.createdAt, DateTime.now).getMinutes

  val reply: Elem =
    <a href={W.hrefMicroChat(chat.creator, chat.id)}>
      {"(" + chat.replayCount + ")回复"}
    </a>
}
