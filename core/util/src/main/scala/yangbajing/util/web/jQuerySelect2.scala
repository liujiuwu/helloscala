package yangbajing.util.web

import net.liftweb.http.js.{JsMember, JsExp}

trait jQuerySelect2[Self] {
  this: jQueryBase[Self] =>

  def select2(): Self = {
    _jsExps += new JsExp with JsMember {
      def toJsCmd: String = "select2()"
    }
    self
  }

  def select2(exp: JsExp): Self = {
    _jsExps += Select2(exp)
    self
  }

  protected case class Select2(value: JsExp) extends JsExp with JsMember {
    def toJsCmd = "select2(" + value.toJsCmd + ")"
  }

}
