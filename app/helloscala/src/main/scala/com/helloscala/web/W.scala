package com.helloscala.web

import net.liftweb.http.{Templates, RequestVar, SessionVar}
import net.liftweb.common.{Box, Empty}

import com.helloscala.model.MUser

object W {
  object theAccount extends SessionVar[Box[MUser]](Empty)

  object reqUser extends RequestVar[Box[MUser]](_findUser)

  def _findUser: Box[MUser] =
    H.param("user_id").flatMap(MUser.find(_))

  def templateSignUp = Templates(List("c", "_sign_up")).openOrThrowException("/c/_sign_up not found!")

  def templateSignIn = Templates(List("c", "_sign_in")).openOrThrowException("/c/_sign_in not found!")
}
