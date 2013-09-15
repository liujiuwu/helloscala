package com.helloscala.web
package snippet

import scala.xml.{Unparsed, NodeSeq}

import net.liftweb.util.Helpers._
import net.liftweb.http.{S, DispatchSnippet}

object SessionSnippet extends DispatchSnippet {

  def dispatch = {
    case "html5Shiv" => html5Shiv
    case "respond" => respond
    case "title" => title
    case "page404" => page404
    case "page500" => page500
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

  def title(nodeSeq: NodeSeq): NodeSeq = 
    ("* *" #> "Hello, Scala!") apply nodeSeq

  def html5Shiv(nodeSeq: NodeSeq): NodeSeq =
    Unparsed( """<!--[if lt IE 9]><script src="/asserts/js/html5shiv.js"></script><![endif]-->""")

  def respond(nodeSeq: NodeSeq): NodeSeq =
    Unparsed( """<!--[if lt IE 9]><script src="/asserts/js/respond/respond.min.js"></script><![endif]-->""")

}
