package com.helloscala

import yangbajing.util.web._
import yangbajing.util.web.bootstrap.BootstrapH

import net.liftweb.http.js.JsExp
import net.liftweb.http.js.jquery.JqJE

package object web extends WebImport {

  val H = new BaseH with BootstrapH

  case class jQuery(exp: JsExp) extends jQueryBase[jQuery] with jQueryBootstrap[jQuery] with jQuerySelect2[jQuery] {
    val jq = JqJE.Jq(exp)

    protected def self: jQuery = this
  }

  @inline
  def $(exp: String) = jQuery(exp)

  @inline
  def $(exp: JsExp) = jQuery(exp)

}
