package yangbajing.util.web.bootstrap

import net.liftweb.http.SHtml.ElemAttr
import scala.xml.{ NodeSeq, Elem }
import net.liftweb.http.js.JsCmd
import net.liftweb.util.Helpers._
import scala.Some
import yangbajing.util.web.BaseH

trait BootstrapH {
  this: BaseH =>

  /**
   *
   * @param id
   * @param buttonContent  (<i class="icon-minus"></i> ++ Text("删除")
   * @param modalTitle
   * @param modalBody
   * @param cssClass
   * @param func
   * @return
   */
  def deleteButton(id: String, buttonContent: NodeSeq, modalTitle: String, modalBody: String, cssClass: String = "btn-link btn data-remove")(func: => JsCmd) = {
    val modalId = nextFuncName

    val cancel = <a href="#" class="btn" data-dismiss="modal">取消</a>

    val delete =
      ajaxA("删除", Some("delete?id=" + id), () => func,
        "class" -> "btn btn-danger", "data-dismiss" -> "modal")

    <button class={ cssClass } data-toggle="modal" href={ "#" + modalId }>
      { buttonContent }
    </button> ++
      modalDialog(modalId, <h3>
                             { modalTitle }
                           </h3>, <div class="alert alert-error">
                                    { modalBody }
                                  </div>, cancel ++ delete)
  }

  def modalDialog(id: String, header: NodeSeq, body: NodeSeq, footer: NodeSeq): NodeSeq =
    <div class="modal fade" id={ id }>
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>{ header }
          </div>
          <div class="modal-body" style="min-height:120px;">
            { body }
          </div>
          <div class="modal-footer">
            { footer }
          </div>
        </div>
      </div>
    </div>

  import net.liftweb.http.js.JsCmds
  import net.liftweb.http.js.JE

  def deleteAction(id: String, trId: String, title: String, body: String, cssClass: String = "text-danger")(func: () => JsCmd) = {
    val modalId = nextFuncName

    val cancel = <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>

    val delete =
      ajaxA("删除", Some("delete?id=" + id), () => func() & JE.JsRaw("$('#" + modalId + "').modal('hide')").cmd,
        "class" -> "btn btn-danger", "data-dismiss" -> "modal")

    <a class={ cssClass } data-toggle="modal" href={ "#" + modalId }>删除</a> ++
      modalDialog(
        modalId,
        <h3>
          { title }
        </h3>,
        <div class="alert alert-error">
          { body }
        </div>,
        cancel ++ delete)
  }

  def label[T <: AnyVal](v: T, attrs: ElemAttr*): Elem =
    label(v.toString, attrs: _*)

  def label(label: => String, attrs: ElemAttr*): Elem =
    <span class="label">
      { label }
    </span>

  def labelSuccess[T <: AnyVal](v: T, attrs: ElemAttr*): Elem =
    labelSuccess(v.toString, attrs: _*)

  def labelSuccess(label: => String, attrs: ElemAttr*) =
    <span class="label label-success">
      { label }
    </span>

  def labelWarning[T <: AnyVal](v: T, attrs: ElemAttr*): Elem =
    labelWarning(v.toString, attrs: _*)

  def labelWarning(label: => String, attrs: ElemAttr*) =
    <span class="label label-warning">
      { label }
    </span>

  def labelInfo[T <: AnyVal](v: T, attrs: ElemAttr*): Elem =
    labelInfo(v.toString, attrs: _*)

  def labelInfo(label: => String, attrs: ElemAttr*) =
    <span class="label label-info">
      { label }
    </span>

  def labelDanger[T <: AnyVal](v: T, attrs: ElemAttr*): Elem =
    labelInfo(v.toString, attrs: _*)

  def labelDanger(label: => String, attrs: ElemAttr*) =
    <span class="label label-danger">
      { label }
    </span>

  def badge[T <: AnyVal](v: T, attrs: ElemAttr*): Elem =
    badge(v.toString, attrs: _*)

  def badge(v: => String, attrs: ElemAttr*) =
    <span class="badge">
      { v }
    </span>

}
