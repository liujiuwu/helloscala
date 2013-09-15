package com.helloscala.model

import yangbajing.persistence.SquerylEntrypoint._
import org.squeryl.Schema

object Entities extends Schema{
  val users = table[MUser]("user_")

  val userPasswords = table[MUserPassword]("user_password_")
}
