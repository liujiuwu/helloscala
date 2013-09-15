package com.helloscala.web
package snippet

import net.liftweb.http.DispatchSnippet
import net.liftweb.util.Helpers._

import scala.xml.NodeSeq

object AjaxFileupload extends DispatchSnippet {
  def dispatch = {
    case "detail" => detail
  }

  def detail(nodeSeq: NodeSeq): NodeSeq = {
    val cssSel =
      "#resource-file [data-url+]" #> "yangbajing"

    cssSel(nodeSeq)
  }

}
