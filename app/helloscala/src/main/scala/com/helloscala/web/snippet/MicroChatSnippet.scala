package com.helloscala.web
package snippet

import scala.xml.NodeSeq

import net.liftweb.common.{Empty, Failure, Loggable, Full}
import net.liftweb.http.{SHtml, DispatchSnippet}
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds

import com.helloscala.model.MMicroChat
import com.helloscala.service.MicroChatManager
import com.helloscala.helper.MicroChat
import yangbajing.util.Imports.Y

object MicroChatSnippet extends DispatchSnippet with Loggable {
  def dispatch = {
    case "global" => global
    case "detail" => detail
  }

  def global(nodeSeq: NodeSeq): NodeSeq =
    W.theAccount.is match {
      case Full(account) =>
        var content = ""

        val cssSel =
          "@content" #> SHtml.text(content, content = _) &
            "@submit" #> SHtml.hidden(() => {
              MMicroChat.create(account.id, content) match {
                case Full(mc) =>
                  MicroChatManager ! MicroChat(mc)

                case Failure(msg, e, _) =>
                  val errorMsg = e openOr msg
                  logger.error(msg)

                case _ =>
                  logger.error("创建MicroChat失败")
              }
              JsCmds.Noop
            })

        SHtml ajaxForm cssSel(nodeSeq)

      case _ =>
        NodeSeq.Empty
    }

  def detail(nodeSeq: NodeSeq): NodeSeq = {
    val cssSel = for (
      user <- W.reqUser.is;
      (mc, mcs) <- W.reqMicroChat
    ) yield {
      val accountHref = W.hrefAccount(mc.creator)

      val replaySels =
        "li" #> mcs.map {
          mc =>
            ".portrait *" #> MicroChatHelpers.portrait(mc) &
              "@creator *" #> mc.creator &
              "@creator [href]" #> accountHref &
              "@content *" #> mc.content &
              "@time" #> s"${Y.betweenNow(mc.createdAt)}分钟前"
        }

      ".breadcrumb" #> (
        "@account *" #> user.nick.getOrElse(user.id) &
          "@account [href]" #> accountHref
        ) &
        ".portrait *" #> MicroChatHelpers.portrait(mc) &
        "@chat-content *" #> mc.content &
        "@created-at *" #> Y.formatDateTime.print(mc.createdAt) &
        "#micro-chat-replays" #> replaySels
    }

    cssSel match {
      case Full(sel) =>
        sel(nodeSeq)

      case Failure(msg, e, _) =>
        e.foreach(_.printStackTrace)
        val errorMsg = e.map(_.getLocalizedMessage) openOr msg
        logger.error(errorMsg)

        <h1 class="text-danger">
          {errorMsg}
        </h1>

      case Empty =>
        NodeSeq.Empty
    }
  }

}
