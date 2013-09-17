package com.helloscala.helper

import scala.xml.Elem

import org.joda.time.{Minutes, DateTime}

import com.helloscala.model.MMicroChat

case class MicroChat(chat: MMicroChat) {

  val portrait: Elem =
    <a href={s"/u/${chat.creator}/"}>
      <img src={s"/upload/user/${chat.creator}/user-32.png"}/>
    </a>

  val minutes: Int = Minutes.minutesBetween(chat.createdAt, DateTime.now).getMinutes

  val reply: Elem =
    <a href={s"/u/${chat.creator}/micro_chat/${chat.id}"}>
      {"(" + chat.replayCount + ")回复"}
    </a>
}
