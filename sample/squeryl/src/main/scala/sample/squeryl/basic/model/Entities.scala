package sample.squeryl.basic.model

import org.squeryl.Schema

import sample.squeryl.basic.helper.SquerylEntrypoint._

object Entities extends Schema {
  val users = table[User]("user")

  val userPasswords = table[UserPassword]("user_password")

  override def defaultLengthOfString: Int = 8192
}
