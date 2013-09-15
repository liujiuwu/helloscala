package yangbajing.util.web

import net.liftweb.util.CanBind
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers.strToCssBindPromoter
import net.liftweb.http.js.{JE, JsExp}

trait WebImport {
  private val _selRegex = new scala.util.matching.Regex( """(\S)+""")
  private val _selMatch = Set('*', '-', '[', '^')

  implicit final class YStringToCssSel(exp: String) {
    def #>>[T](content: => T)(implicit computer: CanBind[T]): CssSel = {
      val selects = _selRegex.findAllMatchIn(exp).map(_.matched).toList

      val (init, last) =
        if (_selMatch.contains(selects.last.head))
          selects.dropRight(2) -> selects.takeRight(2).mkString(" ")
        else
          selects.dropRight(1) -> selects.takeRight(1).mkString

      init.foldRight(last #> content)((s, c) => s #> c)
    }
  }

}

