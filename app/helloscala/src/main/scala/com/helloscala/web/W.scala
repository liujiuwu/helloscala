package com.helloscala.web

import net.liftweb.http._
import net.liftweb.common.{Full, Box, Empty}
import net.liftweb.sitemap.Loc.TestAccess

import com.helloscala.model.{MArticle, MMicroChat, Account, MUser}
import net.liftweb.util.Helpers

object W {


  object reqArticle extends RequestVar[Box[MArticle]](_findArticle)

  object reqUser extends RequestVar[Box[MUser]](_findUser)

  object reqMicroChat extends RequestVar[Box[(MMicroChat, List[MMicroChat])]](_findMicroChat)

  object theAccount extends SessionVar[Box[Account]](Empty)

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

  def idEquals(id: String): Boolean =
    W.theAccount.is exists (account => account.user.spaceName.exists(_ == id) || account.id == id)

  def uriSpace: String = {
    val uris = S.uri.split('/')
    if (uris.length > 2 && uris(1) == "u") Helpers.urlDecode(uris(2))
    else ""
  }

  def hrefAccount(account: String) =
    s"/u/${account}"

  def hrefMicroChat(account: String, id: Long) =
    s"/u/${account}/micro_chat/${id}"

  def templateSignUp =
    Templates(List("c", "_sign_up")).openOrThrowException("/c/_sign_up not found!")

  def templateSignIn =
    Templates(List("c", "_sign_in")).openOrThrowException("/c/_sign_in not found!")

  private[this] def _findArticle: Box[MArticle] =
    H.paramLong("article_id").flatMap(MArticle.findOneById(_))

  private[this] def _findUser: Box[MUser] =
    H.param("user_id").flatMap(MUser.findOne(_))

  private[this] def _findMicroChat: Box[(MMicroChat, List[MMicroChat])] =
    for (
      userId <- H.param("user_id") ?~ "账号不存在";
      microChatId <- H.paramLong("micro_chat_id");
      microChat <- MMicroChat.findAllById(microChatId) ?~ "当前微聊和账号不能对应" if microChat._1.creator == userId
    ) yield microChat

}
