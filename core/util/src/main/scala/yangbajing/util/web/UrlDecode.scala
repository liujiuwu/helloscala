package yangbajing.util.web

import net.liftweb.util.Helpers.urlDecode

object UrlDecode {
  def unapply(v: String): Option[String] =
    Some(urlDecode(v))
}
