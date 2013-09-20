package yangbajing.util

import scala.xml.NodeSeq

import org.joda.time.DateTime

import net.liftweb.util.Helpers._

import yangbajing.util.Imports.Y
import yangbajing.util.data.CreatedAt

trait Pager[T <: CreatedAt, ID] {
  val limit: Int

  val id: Option[ID]

  /**
   * 数据的总行数
   */
  val total: Long

  /**
   * 当前页的数据
   */
  val page: List[T]

  def pageUri(uri: String, dt: Option[DateTime], compare: Int): String =
    dt match {
      case Some(v) => "%s?at=%d&limit=%d&compare=%d" format(uri, urlEncode(Y.formatDateTime.print(v)), limit, compare)
      case None => s"%s?limit=${limit}&compare=${compare}"
    }

  def pager(
             uri: String,
             ulClass: String = "pager"): NodeSeq = {

    val previousSel =
      id match {
        case None => ".previous [class+]" #> "disabled"
        case Some(dt) => "a [href]" #> pageUri(uri, page.headOption.map(_.createdAt), CompareCode.Gt.id)
      }

    val nextSel =
      if (page.size < limit) ".next [class+]" #> "disabled"
      else "a [href]" #> pageUri(uri, page.lastOption.map(_.createdAt), CompareCode.Lte.id)


    val cssSel =
      "ul [class]" #> ulClass &
        ".previous" #> previousSel &
        ".next" #> nextSel

    cssSel(Pager.nodeSeq)
  }

}

object Pager {
  private val nodeSeq =
    <ul>
      <li class="previous">
        <a href="#">
          &larr;
          更早</a>
      </li>
      <li class="next">
        <a href="#">更新
          &rarr;
        </a>
      </li>
    </ul>
}
