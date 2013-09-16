package com.helloscala.web.snippet

import java.text.NumberFormat
import java.util.Locale

import scala.xml.{Text, NodeSeq}

import net.liftweb.http.{SHtml, S, WiringUI, DispatchSnippet}
import net.liftweb.util.Helpers._
import net.liftweb.util.{Cell, ValueCell}
import net.liftweb.http.js.JsCmd

object WiringSnippet extends DispatchSnippet {
  def dispatch = {
    case "render" => render
  }

  def render(nodeSeq: NodeSeq): NodeSeq = {
    val cost = ValueCell(1.99)
    val quantity = ValueCell(1)
    val subtotal = quantity.lift(cost)(_ + _)

    val formatter = NumberFormat.getCurrencyInstance(Locale.SIMPLIFIED_CHINESE)

    def currency(cell: Cell[Double]): NodeSeq => NodeSeq =
      WiringUI.toNode(cell)((value, ns) =>
        (".amount *" #> Text(formatter format value)) apply ns)

    def increment(): JsCmd = {
      quantity.atomicUpdate(_ + 1)
      S.notice("Added One")
    }

    val cssSel =
      "#add [onclick]" #> SHtml.ajaxInvoke(increment) &
        "#quantity *" #> WiringUI.asText(quantity) &
        "#subtotal *" #> currency(subtotal)

    cssSel(nodeSeq)
  }

}
