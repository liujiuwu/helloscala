package com.helloscala.web

import net.liftweb.http._
import net.liftweb.common.{Full, Box, Empty}
import net.liftweb.sitemap.Loc.TestAccess

import com.helloscala.model.{MMicroChat, Account, MUser}

object W {
  object theAccount extends SessionVar[Box[Account]](Empty)

  object reqUser extends RequestVar[Box[MUser]](_findUser)

  object reqMicroChat extends RequestVar[Box[(MMicroChat, List[MMicroChat])]](_findMicroChat)

  def testSession =
    TestAccess(() =>
      theAccount.is match {
        case Full(account) => Empty
        case _ => Full(RedirectResponse(H.gotoPage openOr "/c/sign_in"))
      })

  def testAdmin =
    TestAccess(() =>
      theAccount.is match {
        case Full(account) if account.user.isAdmin() => Empty
        case _ => Full(RedirectResponse(H.gotoPage openOr "/c/sign_in"))
      })

  def signOut =
    TestAccess(() => {
      W.theAccount.remove()
      Full(RedirectResponse("/index"))
    })

  def saveSessionAndCookie(user: MUser, remember: Boolean = true, data: AnyRef = null) {
    assert(user ne null)

    val account = Account(user.id, user, Option(data))
    W.theAccount(Full(account))

    S.redirectTo(S.param("goto_page") openOr "/index")
  }

  def hrefAccount(account: String) =
    s"/u/${account}"

  def hrefMicroChat(account: String, id: Long) =
    s"/u/${account}/micro_chat/${id}"

  def templateSignUp =
    Templates(List("c", "_sign_up")).openOrThrowException("/c/_sign_up not found!")

  def templateSignIn =
    Templates(List("c", "_sign_in")).openOrThrowException("/c/_sign_in not found!")

  private[this] def _findUser: Box[MUser] =
    H.param("user_id").flatMap(MUser.find(_))

  private[this] def _findMicroChat: Box[(MMicroChat, List[MMicroChat])] =
    for (
      userId <- H.param("user_id") ?~ "账号不存在";
      microChatId <- H.paramLong("micro_chat_id");
      microChat <- MMicroChat.findAllById(microChatId) ?~ "当前微聊和账号不能对应" if microChat._1.creator == userId
    ) yield microChat

}
