package yangbajing.util.web

import scala.xml.{Elem, Text, NodeSeq}

import net.liftweb.common.Box

import net.liftweb.util.Helpers._
import net.liftweb.http.js.{JsCmd, JsMember, JsExp}
import net.liftweb.http.js.jquery.JqJE
import net.liftweb.http.js.JsCmds.HasTime

trait jQueryBase[Self] {
  def jq: JsExp

  protected def self: Self

  //  type Self

  protected val _jsExps = collection.mutable.ArrayBuffer.empty[JsMember]

  def cmd: JsCmd = _jsExps.foldLeft[JsExp](jq)(_ ~> _).cmd

  def _enabled(): Self =
    removeAttr("disabled")

  def _disabled(): Self =
    attr("disabled", "disabled")


  def show(): Self = show(None)

  def show(time: Option[TimeSpan]): Self = {
    _jsExps += JqShow(time)
    self
  }

  def hide(): Self = hide(None)

  def hide(time: Option[TimeSpan]): Self = {
    _jsExps += JqHide(time)
    self
  }

  def html(elems: Seq[Elem]): Self = {
    _jsExps += JqJE.JqHtml(NodeSeq.fromSeq(elems))
    self
  }

  def html(content: NodeSeq): Self = {
    _jsExps += JqJE.JqHtml(content)
    self
  }

  def html(content: String): Self =
    html(if (content == "") NodeSeq.Empty else Text(content))

  def html[T <: AnyVal](content: T): Self =
    html(Text(content.toString))

  def html(): Self = {
    _jsExps += JqJE.JqHtml()
    self
  }

  def append(content: NodeSeq): Self = {
    _jsExps += JqJE.JqAppend(content)
    self
  }

  def append(value: String): Self =
    append(Text(value))

  def appendTo(content: NodeSeq): Self = {
    _jsExps += JqJE.JqAppendTo(content)
    self
  }

  def prepared(content: NodeSeq): Self = {
    _jsExps += JqJE.JqPrepend(content)
    self
  }

  def preparedTo(content: NodeSeq): Self = {
    _jsExps += JqJE.JqPrependTo(content)
    self
  }

  def value(v: String): Self = {
    _jsExps += SetValue(v)
    self
  }

  def value(v: JsExp): Self = {
    _jsExps += SetValue(v)
    self
  }

  def value(): Self = {
    _jsExps += Value()
    self
  }

  def attr(key: String): Self = {
    _jsExps += JqJE.JqGetAttr(key)
    self
  }

  def attr(key: String, value: JsExp): Self = {
    _jsExps += JqJE.JqAttr(key, value)
    self
  }

  def removeAttr(key: String): Self = {
    _jsExps += RemoveAttr(key)
    self
  }

  def getClass(key: String): Self = {
    _jsExps += JqJE.JqGetAttr(key)
    self
  }

  def setClass(value: JsExp): Self = {
    attr("class", value)
    self
  }

  def remove(): Self = {
    _jsExps += JqJE.JqRemove()
    self
  }

  def removeClass(value: String): Self = {
    _jsExps += JqRemoveClass(value)
    self
  }

  def addClass(value: String): Self = {
    _jsExps += JqAddClass(value)
    self
  }

  def replaceWith(content: NodeSeq): Self = {
    _jsExps += JqJE.JqReplace(content)
    self
  }

  private case class RemoveAttr(key: String) extends JsExp with JsMember {
    def toJsCmd = "removeAttr(" + key.encJs + ")"
  }

  private case class SetValue(value: JsExp) extends JsExp with JsMember {
    def toJsCmd = "val(" + value.toJsCmd + ")"
  }

  private case class Value() extends JsExp with JsMember {
    def toJsCmd = "val()"
  }

  private case class JqRemoveClass(value: String) extends JsExp with JsMember {
    def toJsCmd = "removeClass(" + value.encJs + ")"
  }

  private case class JqAddClass(value: String) extends JsExp with JsMember {
    def toJsCmd = "addClass(" + value.encJs + ")"
  }

  private case class JqShow(time: Box[TimeSpan]) extends JsExp with JsMember with HasTime {
    def toJsCmd = "show(" + timeStr + ")"
  }

  private case class JqHide(time: Box[TimeSpan]) extends JsExp with JsMember with HasTime {
    def toJsCmd = "hide(" + timeStr + ")"
  }

}
