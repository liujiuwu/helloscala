package com.helloscala.web.snippet

import scala.xml.NodeSeq

import net.liftweb.http.{SHtml, DispatchSnippet}
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds

object SearchSnippet extends DispatchSnippet {
  def dispatch = {
    case "mainSearch" => mainSearch
    case "advancedSearch" => advancedSearch
  }

  def mainSearch(nodeSeq: NodeSeq): NodeSeq = {
    var content = ""

    val cssSel =
      ":text" #> SHtml.text(content, content = _) &
        "@submit" #> SHtml.hidden(() => {
          JsCmds.RedirectTo("/page/search/index?v=" + urlEncode(content))
        })

    cssSel(nodeSeq)
  }

  def advancedSearch(nodeSeq: NodeSeq): NodeSeq = {

    val cssSel =
      "" #> ""

    cssSel(nodeSeq)
  }
}
