package com.helloscala.web

import net.liftweb.util.Helpers

object UrlDecode {
  def unapply(v: String): Option[String] =
    Some(Helpers.urlDecode(v))
}
