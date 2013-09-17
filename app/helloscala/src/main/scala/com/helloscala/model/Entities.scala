package com.helloscala.model

import yangbajing.persistence.SquerylEntrypoint._
import org.squeryl.Schema

object Entities extends Schema{
  val users = table[MUser]("user_")

  val userPasswords = table[MUserPassword]("user_password_")

  val microChats = table[MMicroChat]("micro_chat")

  override def defaultLengthOfString: Int = 1024
}
