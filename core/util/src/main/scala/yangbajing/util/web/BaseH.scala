package yangbajing.util.web

import scala.collection.Iterable

import scala.xml.{Elem, NodeSeq, Text}

import org.joda.time.{LocalTime, LocalDate, DateTime}

import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http.S
import net.liftweb.http.SHtml
import net.liftweb.http.SHtml.ElemAttr.pairToBasic
import net.liftweb.http.Templates
import net.liftweb.http.js.JE
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JsExp.strToJsExp
import net.liftweb.http.js.jquery.JqJsCmds
import net.liftweb.util.Helpers._
import net.liftweb.util.CssSel

import yangbajing.util.BaseRandom
import yangbajing.util.Imports._
import org.bson.types.ObjectId

trait BaseH extends BaseRandom {
  val _limitOpts = List(10, 25, 50, 100)
  val defaultLimit = 15
  val pagingLimitOpts = _limitOpts.map {
    i =>
      val v = i.toString
      v -> v
  }

  val DEFAULT_RADIO_LEBAL_CLASS = "radio-inline"

  @inline
  def href(v: String, name: String, attrs: SHtml.ElemAttr*): Elem =
    href(v, Text(name), attrs: _*)

  def href(v: String, name: NodeSeq, attrs: SHtml.ElemAttr*): Elem =
    attrs.foldLeft(
      <a href={v}>
        {name}
      </a>
    )(_ % _)

  @inline
  def hrefBlank(v: String, name: String, attrs: SHtml.ElemAttr*): Elem =
    hrefBlank(v, Text(name), attrs: _*)

  def hrefBlank(href: String, content: NodeSeq, attrs: SHtml.ElemAttr*): Elem =
    attrs.foldLeft(
      <a href={href} target="_blank">
        {content}
      </a>
    )(_ % _)

  @inline
  def paramDateTime(key: String): Option[DateTime] =
    S.param(key).toOption.filterNot(_.isEmpty).flatMap(v => Y.tryoption(Y.formatDateTime.parseDateTime(urlDecode(v))))

  @inline
  def paramDate(key: String): Option[LocalDate] =
    S.param(key).toOption.filterNot(_.isEmpty).flatMap(v => Y.tryoption(Y.formatDate.parseLocalDate(urlDecode(v))))

  @inline
  def paramTime(key: String): Option[LocalTime] =
    S.param(key).toOption.filterNot(_.isEmpty).flatMap(v => Y.tryoption(Y.formatTime.parseLocalTime(urlDecode(v))))

  @inline
  def param(key: String): Option[String] =
    S.param(key).toOption.filterNot(_.isEmpty).map(urlDecode)

  @inline
  def param(key: String, deft: Int): Int =
    paramInt(key).getOrElse(deft)

  @inline
  def paramInt(key: String): Option[Int] =
    S.param(key).flatMap(asInt).toOption

  @inline
  def param(key: String, deft: Double): Double =
    paramDouble(key).getOrElse(deft)

  @inline
  def paramDouble(key: String): Option[Double] =
    S.param(key).flatMap(asDouble).toOption

  @inline
  def param(key: String, deft: Long): Long =
    paramLong(key) getOrElse deft

  @inline
  def paramLong(key: String): Option[Long] =
    S.param(key).flatMap(asLong).toOption

  @inline
  def param(key: String, deft: => Boolean): Boolean =
    paramBoolean(key).getOrElse(deft)

  @inline
  def paramBoolean(key: String): Option[Boolean] =
    S.param(key).flatMap(asBoolean).toOption

  @inline
  def param(key: String, deft: => ObjectId): ObjectId =
    paramObjectId(key).getOrElse(deft)

  @inline
  def paramObjectId(key: String): Option[ObjectId] =
    S.param(key) match {
      case Full(id) if ObjectId.isValid(id) => Some(new ObjectId(id))
      case _ => None
    }

  def uriPaths(uri: String): Vector[String] = {
    val uri = S.uri
    val paths = uri.split('/').drop(1).toVector

    if (paths.size == 0) Vector("index")
    else if (uri.lastOption.exists(_ == '/')) paths :+ "index"
    else paths
  }

  /**
   * 返回的链接已 urlEncode
   * @return
   */
  def gotoPage: Option[String] =
    S.uriAndQueryString.openOr(S.uri) match {
      case "/" => None
      case uri if uri.startsWith("/index") => None
      case uri => Some(urlEncode(uri))
    }

  def uneditableInput[T <: AnyVal](value: T, attrs: (String, String)*): NodeSeq =
    uneditableInput(value.toString, attrs: _*)

  def uneditableInput(content: String, attrs: (String, String)*): NodeSeq =
    uneditableInput(Text(content), attrs: _*)

  def uneditableInput(content: NodeSeq, attrs: (String, String)*): NodeSeq =
    attrs.foldLeft(<input type="text" disabled="disabled" class="form-control" value={content}/>)(_ % _)

  def uneditableRadio(descs: Iterable[String], deft: Box[String], labelClass: String = "radio inline"): NodeSeq =
    descs.map {
      desc =>
        <label class={labelClass}>
          <input type="radio" disabled="disabled"/>{desc}
        </label>
    }.foldLeft(NodeSeq.Empty)(_ ++ _)

  def uneditableTextarea(content: String, attrs: SHtml.ElemAttr*): NodeSeq =
    uneditableTextarea(Text(content), attrs: _*)

  def uneditableTextarea(content: NodeSeq, attrs: SHtml.ElemAttr*): NodeSeq =
    attrs.foldLeft(<textarea disabled="disabled" class="form-control">
      {content}
    </textarea>)(_ % _)

  def uneditableSelect(opts: Seq[(String, String)], deflt: Seq[String], attrs: SHtml.ElemAttr*): NodeSeq = {
    val options =
      opts.map {
        case (value, desc) =>
          if (deflt.contains(value))
            <option value={value} selected="selected">
              {desc}
            </option>
          else
            <option value={value}>
              {desc}
            </option>
      }

    val node =
      if (deflt.size > 1)
        <select disabled="disabled" multiple="multiple">
          {options}
        </select>
      else
        <select disabled="disabled">
          {options}
        </select>

    attrs.foldLeft(node)(_ % _)
  }

  def jqFileupload(id: String, url: String, dataType: String = "json"): JsCmd = {
    val js = """
$('#%s').fileupload({
  dataType: '%s',
  url: '%s',
  dropZone: $('#image'),
  done: function(e, data) {
    $('#image').html('<img src='/r/ad/serving/' + data.result.name height="480" width="640" />);
  }
})
             """.format(id, dataType, url)

    JE.JsRaw(js).cmd
  }

  def script(value: String, func: String => Any, attrs: SHtml.ElemAttr*): Elem =
    S.fmapFunc(func)(funcName =>
      attrs.foldLeft(<script type="text/plain" name={funcName}>
        {if (value eq null) "" else value}
      </script>)(_ % _))

  def a(func: () => JsCmd, content: NodeSeq, attrs: SHtml.ElemAttr*) = {
    val modalId = nextFuncName

      <a/> % attrs.head

    attrs.foldLeft(S.fmapFunc((func))(name =>
      <a href={"#" + modalId} data-toggle="modal" onclick={SHtml.makeAjaxCall(JE.Str(name + "=true")).toJsCmd + "; return false;"}>
        {content}
      </a>))(_ % _)
  }

  def ajaxTextareaDialog(header: String, body: String, attrs: SHtml.ElemAttr*)(func: String => JsCmd) = {
    val template = List("_templates", "modal")
    Templates(template) match {
      case Full(nodeSeq) =>
        val modalId = nextFuncName
        val inputId = "input" + modalId

        var tmpMsg = "" // XXX

        val cssSel =
          "#modal [id]" #> modalId &
            "h3 *" #> header &
            "p" #> (SHtml.textarea(body, tmpMsg = _) ++
              SHtml.hidden {
                () =>
                  JsCmds.SetValById(inputId, tmpMsg) &
                    func(tmpMsg)
              }) &
            ".btn-primary [onclick]" #> "$('#%s').click();$('#%s').modal('hide');".format(modalId, modalId)

          <input id={inputId} type="text" value={body}/> ++
          <a href={"#" + modalId}>编辑</a> ++
          SHtml.ajaxForm(cssSel(nodeSeq))

      case _ =>
        Text("templates %s not found!!!" format template)
    }
  }

  /**
   * 此方法需要在页面包括 jquery.blockui.js
   *
   * @param text <button>{ text }</button>
   * @param msg Dialog消息
   * @param func 点击确定按钮后在服务端执行函数
   * @param okClass 确定按钮css
   * @param cancelClass 取消按钮css
   * @param attrs Button按钮的属性
   */
  def ajaxButtonDialog(text: String, msg: String, okClass: String, cancelClass: String, attrs: SHtml.ElemAttr*)(func: => JsCmd) =
    SHtml.ajaxButton(text, () => {
      val ns =
        <h3>
          {msg}
        </h3> ++
          SHtml.ajaxButton("确定", () => func & JsCmds.Run("$.unblockUI();"), "class" -> okClass) ++
          <button onclick="$.unblockUI();" class={cancelClass}>取消</button>

      JqJsCmds.ModalDialog(ns)
    }, attrs: _*)

  def ajaxButtonDialog(text: NodeSeq, msg: NodeSeq, func: () => JsCmd, attrs: SHtml.ElemAttr*) =
    SHtml.ajaxButton(text, () => {
      val ns =
        msg ++
          SHtml.ajaxButton("确定", () => func() & JsCmds.Run("$.unblockUI();"), "class" -> "btn btn-primary btn-small") ++
          <button onclick="$.unblockUI();" class="btn btn-small">取消</button>

      JqJsCmds.ModalDialog(ns)
    }, attrs: _*)

  /**
   * 此方法需要在页面包括 jquery.blockui.js
   */
  def ajaxADialog(text: String, msg: String, okClass: String, cancelClass: String, attrs: SHtml.ElemAttr*)(func: => JsCmd): Elem =
    ajaxADialog(Text(text), Text(msg), okClass, cancelClass, attrs: _*)(func)

  /**
   * 此方法需要在页面包括 jquery.blockui.js
   */
  def ajaxADialog(text: NodeSeq, msg: NodeSeq, okClass: String, cancelClass: String, attrs: SHtml.ElemAttr*)(func: => JsCmd): Elem =
    ajaxA(text, Empty, () => {
      val ns =
        <h3>
          {msg}
        </h3> ++
          SHtml.ajaxButton("确认", () => func & JsCmds.Run("$.unblockUI();"), "class" -> okClass) ++
          <span class="divider">|</span> ++
          <button onclick="$.unblockUI();" class={cancelClass}>取消</button>

      JqJsCmds.ModalDialog(ns)
    }, attrs: _*)

  def ajaxA(func: () => JsCmd, attrs: SHtml.ElemAttr*): Elem =
    ajaxA("", Empty, func, attrs: _*)

  def ajaxA(text: String, func: () => JsCmd, attrs: SHtml.ElemAttr*): Elem =
    ajaxA(text, Empty, func, attrs: _*)

  def ajaxA(text: String, href: Box[String], func: () => JsCmd, attrs: SHtml.ElemAttr*): Elem =
    ajaxA(Text(text), href, func, attrs: _*)

  def ajaxA(text: NodeSeq, href: Box[String], func: () => JsCmd, attrs: SHtml.ElemAttr*): Elem = {
    val elem = S.fmapFunc(S.contextFuncBuilder(func))(name =>
      <a onclick={SHtml.makeAjaxCall(JE.Str(name + "=true")).toJsCmd + "; return false;"}>
        {text}
      </a>)

    val attrsElem = attrs.foldLeft(elem)((elem, attr) => attr(elem))
    SHtml.ElemAttr.pairToBasic("href" -> href.openOr("#"))(attrsElem)
  }

  @inline
  def textAjax(value: String, func: String => JsCmd, attrs: SHtml.ElemAttr*): NodeSeq = {
    SHtml.onEvents("onblur")(func) {
      SHtml.text(value, func, attrs: _*)
    }
  }

  @inline
  def ajaxText(value: String, jsFunc: String => JsCmd, attrs: SHtml.ElemAttr*)(func: String => Any): NodeSeq = {
    SHtml.onEvents("onblur")(jsFunc) {
      SHtml.text(value, func, attrs: _*)
    }
  }

  @inline
  def passwordAjax(value: String, func: String => JsCmd, attrs: SHtml.ElemAttr*): NodeSeq = {
    SHtml.onEvents("onblur")(func) {
      SHtml.password(value, func, attrs: _*)
    }
  }

  @inline
  def ajaxPassword(value: String, jsFunc: String => JsCmd, attrs: SHtml.ElemAttr*)(func: String => Any): NodeSeq = {
    SHtml.onEvents("onblur")(jsFunc) {
      SHtml.password(value, func, attrs: _*)
    }
  }

  @inline
  def textareaAjax(value: String, func: String => JsCmd, attrs: SHtml.ElemAttr*): NodeSeq = {
    SHtml.onEvents("onblur")(func) {
      SHtml.textarea(value, func, attrs: _*)
    }
  }

  @inline
  def ajaxTextarea(value: String, jsFunc: String => JsCmd, attrs: SHtml.ElemAttr*)(func: String => Any): NodeSeq = {
    SHtml.onEvents("onblur")(jsFunc) {
      SHtml.textarea(value, func, attrs: _*)
    }
  }

  def checkbox(value: Boolean, content: NodeSeq, func: Boolean => Any, attrs: SHtml.ElemAttr*): Elem =
    <label class="checkbox-inline">
      {SHtml.checkbox(value, func, attrs: _*)}{content}
    </label>

  @inline
  def ajaxRadio[T](opts: Iterable[T], deft: Box[T], ajaxFunc: T => JsCmd, attrs: SHtml.ElemAttr*): NodeSeq =
    ajaxRadioElem[T](opts.map(v => v -> v.toString), deft, ajaxFunc, attrs: _*)

  def ajaxRadioElem[T](_opts: Iterable[(T, String)], deft: Box[T], func: T => JsCmd, attrs: SHtml.ElemAttr*): NodeSeq = {
    val (opts, desc) = _opts.toSeq.unzip
    val node = SHtml.ajaxRadio(opts, deft, func).toForm

    radio_*(desc, node, attrs)
  }

  def radioObj[T](_opts: Iterable[(T, String)], deft: Box[T], func: Box[T] => Any, attrs: SHtml.ElemAttr*): NodeSeq = {
    val (opts, descs) = _opts.toSeq.unzip
    val node = SHtml.radioElem[T](opts, deft)(func).toForm

    radio_*(descs, node, attrs)
  }

  def radio(_opts: Iterable[String], deft: Box[String], func: String => Any, attrs: SHtml.ElemAttr*): NodeSeq = {
    val opts = _opts.toSeq
    val node = SHtml.radio(opts, deft, func).toForm

    radio_*(opts, node, attrs)
  }

  @inline
  def radioElem[T](_opts: Iterable[T], deft: Box[T], func: Box[T] => Any, attrs: SHtml.ElemAttr*): NodeSeq =
    radioObj[T](_opts.map(v => v -> v.toString), deft, func, attrs: _*)

  protected def radio_*[T](descs: Seq[T], node: NodeSeq, attrs: Seq[SHtml.ElemAttr]): NodeSeq = {
    val spans = descs.zip(node \\ "span")
    val builder = NodeSeq.newBuilder

    spans.foreach {
      case (desc, spans) =>
        builder +=
          attrs.foldLeft(<label class={DEFAULT_RADIO_LEBAL_CLASS}>
            {spans \\ "input"}{desc.toString}
          </label>)(_ % _)
    }

    builder.result()
  }

  def ajaxDate(value: String, func: String => JsCmd, attrs: SHtml.ElemAttr*): NodeSeq = {
    val id: String = Y.nextKey
    val js = "$('#%s').datepicker(imsDatepicker);".format(id)
    S.appendJs(JE.JsRaw(js).cmd)
    SHtml.ajaxText(value, func, "type" -> "hidden", "id" -> s"${id}_hidden") ++
      attrs.foldLeft(<input type="text" id={id} value={value}></input>)(_ % _)
  }

  def ajaxDatetime(value: String, func: String => JsCmd, attrs: SHtml.ElemAttr*): NodeSeq = {
    val id: String = Y.nextKey
    val js = "$('#%s').datetimepicker(imsDatepicker);".format(id)
    S.appendJs(JE.JsRaw(js).cmd)
    SHtml.ajaxText(value, func, "type" -> "hidden", "id" -> s"${id}_hidden") ++
      attrs.foldLeft(<input type="text" id={id} value={value}></input>)(_ % _)
  }

  def date(value: String, func: String => Any, attrs: SHtml.ElemAttr*): NodeSeq = {
    val id: String = Y.nextKey
    val js = "$('#%s').datepicker(imsDatepicker);".format(id)
    S.appendJs(JE.JsRaw(js).cmd)
    SHtml.text(value, func, "type" -> "hidden", "id" -> s"${id}_hidden") ++
      attrs.foldLeft(<input type="text" id={id} value={value}></input>)(_ % _)
  }

  def datetime(value: String, func: String => Any, attrs: SHtml.ElemAttr*): NodeSeq = {
    val id: String = Y.nextKey
    val js = "$('#%s').datetimepicker(imsDatepicker);".format(id)
    S.appendJs(JE.JsRaw(js).cmd)
    SHtml.text(value, func, "type" -> "hidden", "id" -> s"${id}_hidden") ++
      attrs.foldLeft(<input type="text" id={id} value={value}></input>)(_ % _)
  }

}
