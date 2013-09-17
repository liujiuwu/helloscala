package com.helloscala.web
package snippet

import scala.xml.NodeSeq

import net.liftweb.common.{Failure, Loggable, Full}
import net.liftweb.http.{SHtml, DispatchSnippet}
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds

import com.helloscala.model.MMicroChat
import com.helloscala.service.MicroChatManager
import com.helloscala.helper.MicroChat

object MicroChatSnippet extends DispatchSnippet with Loggable {
  def dispatch = {
    case "global" => global
    case "part" => part
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

  def part(nodeSeq: NodeSeq): NodeSeq = {

    val cssSel =
      "" #> ""

    cssSel(nodeSeq)
  }

}
