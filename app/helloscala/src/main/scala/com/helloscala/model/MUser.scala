package com.helloscala.model

import com.helloscala.common.SexCode

import org.joda.time.DateTime
import org.squeryl.KeyedEntity

import yangbajing.persistence.SquerylEntrypoint._
import yangbajing.util.Imports.Y
import net.liftweb.util.StringHelpers
import net.liftweb.common.Box

object MUser {
  def exists(id: String): Boolean =
    transaction(from(Entities.users)(u =>
      where(u.id === id)
        compute (count(u.id))
    )) > 0L

  def find(id: String, password: Option[String] = None): Box[MUser] =
    Y.tryBox {
      require(isAllowedId(id))
      password foreach (v => require(isAllowedPassword(v)))

      transaction(from(Entities.users, Entities.userPasswords)((u, up) =>
        where(
          u.id === id and
            (u.id === up.id) and
            (up.password === password.?)
        ) select (u)
      ).single)
    }

  def persist(user: MUser): Box[Unit] =
    Y.tryBox {
      require(isAllowedId(user.id))

      transaction(Entities.users update user)
    }

  def insert(user: MUser, password: String): Box[MUser] =
    Y.tryBox {
      require(isAllowedId(user.id))
      require(isAllowedPassword(password))
      require(!exists(user.id), s"用户：${user.id} 已存在")

      val (newPassword, salt) = realPassword(password)
      val userPassword = MUserPassword(user.id, newPassword, salt)

      var newUser: MUser = null
      transaction {
        newUser = Entities.users insert user // XXX 不可能返回null
        Entities.userPasswords insert userPassword
      }

      newUser
    }

  /**
   *
   * @param password
   * @return (新的密码, salt值)
   */
  @inline
  def realPassword(password: String): (String, String) = {
    val salt = StringHelpers.randomString(8)
    val newPassword = Y.ySha256(salt + password)

    (newPassword, salt)
  }

  @inline
  def isAllowedId(id: String): Boolean =
    (id ne null) && Y.emailValidate(id)

  @inline
  def isAllowedPassword(password: String): Boolean =
    (password ne null) && (password.length > 5)

}

case class MUser(
                  // must email address
                  id: String,

                  nick: Option[String],

                  sex: Option[SexCode.Value] = None,

                  createdAt: DateTime = DateTime.now()) extends KeyedEntity[String] {

  def this() {
    this("", None)
  }
}

case class MUserPassword(id: String, password: String, salt: String) extends KeyedEntity[String]
