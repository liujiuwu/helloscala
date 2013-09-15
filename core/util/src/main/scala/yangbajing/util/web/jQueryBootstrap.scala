package yangbajing.util.web

import net.liftweb.http.js.{JsMember, JsExp}

trait jQueryBootstrap[Self] {
  this: jQueryBase[Self] =>

  def collapse(value: JsExp): Self = {
    _jsExps += Collapse(value)
    self
  }

  def modal(exp: JsExp): Self = {
    _jsExps += Model(exp)
    self
  }

  protected case class Collapse(key: JsExp) extends JsExp with JsMember {
    def toJsCmd = "collapse(" + key.toJsCmd + ")"
  }

  protected case class Model(value: JsExp) extends JsExp with JsMember {
    def toJsCmd = "modal(" + value.toJsCmd + ")"
  }

}
