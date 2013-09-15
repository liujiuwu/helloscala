package com.helloscala.web
package snippet

import scala.xml.{Unparsed, NodeSeq}

import net.liftweb.http.{SHtml, S, DispatchSnippet}
import net.liftweb.common.{Failure, Full}
import net.liftweb.util.Helpers._
import net.liftweb.util.ClearNodes
import net.liftweb.http.js.{JE, JsCmds, JsCmd}
import com.helloscala.model.MUser
import yangbajing.util.Imports.Y

object SessionSnippet extends DispatchSnippet {

  def dispatch = {
    case "html5Shiv" => html5Shiv
    case "respond" => respond
    case "navbarTop" => navbarTop
    case "title" => title
    case "signIn" => signIn
    case "signUp" => signUp
    case "forgotPassword" => forgotPassword
    case "page404" => page404
    case "page500" => page500
  }

  def signIn(nodeSeq: NodeSeq): NodeSeq = {
    var id = ""
    var pwd = ""

    def idJs(v: String): JsCmd =
      if (MUser.isAllowedId(v))
        JE.Call("formControlSuccess", "#sign-in-control-id").cmd &
          $("#sign-in-control-submit :submit").removeDisabled().cmd
      else
        JE.Call("formControlError", "#sign-in-control-id", "账号不符合要求！").cmd &
          $("#sign-in-control-submit :submit").disabled().cmd

    def submitFunc: JsCmd =
      MUser.find(id, Some(pwd)) match {
        case Full(user) =>
          W.saveSessionAndCookie(user)
          JsCmds.Alert("登录成功。") &
            JsCmds.RedirectTo(S.param("goto_page") openOr "/index")

        case Failure(msg, e, _) =>
          val errorMsg = e.map(_.getLocalizedMessage) openOr msg
          JE.Call("formControlError", "#sign-in-control-submit", errorMsg).cmd

        case _ =>
          JE.Call("formControlError", "#sign-in-control-submit", "登录失败，请检查账号或密码是否正确！").cmd
      }


    val cssSel =
      "@account" #> H.ajaxText(id, idJs _)(id = _) &
        "@password" #> SHtml.password(pwd, pwd = _) &
        "@submit" #> SHtml.hidden(() => submitFunc)

    cssSel(nodeSeq)
  }

  def signUp(nodeSeq: NodeSeq): NodeSeq = {
    var id = ""
    var pwd = ""
    var pwd2 = ""
    var nick = ""
    var first = true

    def idJs(v: String): JsCmd =
      if (MUser.isAllowedId(v)) {
        if (MUser.exists(v)) JE.Call("formControlError", "#sign-up-control-id", "账号已存在！").cmd
        else JE.Call("formControlSuccess", "#sign-up-control-id").cmd
      } else
        JE.Call("formControlError", "#sign-up-control-id", "账号不符合要求！").cmd

    def funcPwd(v: String): JsCmd = {
      if (MUser.isAllowedPassword(v)) {
        pwd = v
        println("pwd: %s, pwd2: %s" format(pwd, pwd2))
        val cmd = JE.Call("formControlSuccess", "#sign-up-control-pwd").cmd
        if (first) {
          first = false
          cmd
        } else if (v != pwd2)
          JE.Call("formControlError", "#sign-up-control-pwd", "两次密码不匹配！").cmd
        else
          cmd & JE.Call("formControlSuccess", "#sign-up-control-pwd2").cmd
      } else
        JE.Call("formControlError", "#sign-up-control-pwd", "密码不符合要求！").cmd

    }

    def funcPwd2(v: String): JsCmd =
      if (v != pwd)
        JE.Call("formControlError", "#sign-up-control-pwd2", "两次密码不匹配！").cmd
      else if (MUser.isAllowedPassword(v)) {
        pwd2 = v
        println("pwd: %s, pwd2: %s" format(pwd, pwd2))
        JE.Call("formControlSuccess", "#sign-up-control-pwd").cmd &
          JE.Call("formControlSuccess", "#sign-up-control-pwd2").cmd
      } else
        JE.Call("formControlError", "#sign-up-control-pwd2", "密码不符合要求！").cmd

    def submitFunc: JsCmd = {
      val user = MUser(id, nick = Y.option(nick))
      MUser.createAndInsert(user, pwd) match {
        case Full(u) =>
          W.saveSessionAndCookie(user)
          JsCmds.Alert("注册成功。") &
            JsCmds.RedirectTo(S.param("goto_page") openOr "/index")

        case Failure(msg, e, _) =>
          val errorMsg = e.map(_.getLocalizedMessage) openOr msg
          JE.Call("#formControlError", "#sign-up-control-submit", errorMsg).cmd

        case _ =>
          JE.Call("#formControlError", "#sign-up-control-submit", "注册失败").cmd
      }
    }

    val cssSel =
      "@nick" #> SHtml.text(nick, nick = _) &
        "@account" #> H.ajaxText(id, idJs _)(id = _) &
        "@password" #> H.ajaxPassword(pwd, funcPwd _)(pwd = _) &
        "@password2" #> H.ajaxPassword(pwd2, funcPwd2 _)(pwd2 = _) &
        "@submit" #> SHtml.hidden(() => submitFunc)

    cssSel(nodeSeq)
  }

  def forgotPassword(nodeSeq: NodeSeq): NodeSeq = {

    val cssSel =
      "" #> ""

    cssSel(nodeSeq)
  }

  def page404(nodeSeq: NodeSeq): NodeSeq = {
    val title = S.attr("title") or S.param("title") map urlDecode openOr "页面未找到"
    val content = S.attr("content") or S.param("content") map urlDecode openOr ""

    val cssSel =
      "@title *" #> title &
        "@content *" #> content

    cssSel(nodeSeq)
  }

  def page500(nodeSeq: NodeSeq): NodeSeq = {
    val title = S.attr("title") or S.param("title") map urlDecode openOr "内部错误"
    val content = S.attr("content") or S.param("content") map urlDecode openOr ""

    val cssSel =
      "@title *" #> title &
        "@content *" #> content

    cssSel(nodeSeq)
  }

  def navbarTop(nodeSeq: NodeSeq): NodeSeq = {
    val navbarClass = S.attr("class").openOr("")

    val mainMenuSel = W.theAccount.is match {
      case Full(account) =>
        "@account *" #> account.user.nick.getOrElse(account.id) &
          "@not-logged-in" #> ClearNodes

      case _ =>
        "@logged" #> ClearNodes
    }

    val cssSel =
      ".navbar [class+]" #> navbarClass &
        "#main-menu" #> mainMenuSel

    cssSel(nodeSeq)
  }

  def title(nodeSeq: NodeSeq): NodeSeq =
    ("* -*" #> "Hello, Scala!") apply nodeSeq

  def html5Shiv(nodeSeq: NodeSeq): NodeSeq =
    Unparsed( """<!--[if lt IE 9]><script src="/asserts/js/html5shiv.js"></script><![endif]-->""")

  def respond(nodeSeq: NodeSeq): NodeSeq =
    Unparsed( """<!--[if lt IE 9]><script src="/asserts/js/respond/respond.min.js"></script><![endif]-->""")

}
