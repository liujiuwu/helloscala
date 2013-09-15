package yangbajing.util

import scala.xml.Elem

import yangbajing.common.MathHelpers.yMinSwap

trait Pagination[T] {
  /**
   * 当前页码（从1开始计数）
   */
  def curpage: Int

  /**
   * 每页显示多少数据
   */
  def limit: Int

  /**
   * 数据的总行数
   */
  def total: Long

  /**
   * 当前页的数据
   */
  def page: List[T]

  /**
   * 总页数
   */
  def numpages: Int = ((total + limit - 1) / limit).toInt

  // (total / limit).toInt + (if (total % limit > 0) 1 else 0)

  // 数据行实际偏移（用于数据库查询）
  def offset = (curpage - 1) * limit

  /**
   * 每页显示页码数
   */
  val paginglimit: Int = 10

  /**
   * 页码实际偏移
   */
  val pagingoffset: Int = (curpage + paginglimit) / paginglimit

  def pagingend = yMinSwap(pagingoffset + paginglimit - 1, numpages)

  def pageUri(uri: String, _curPage: Int): String =
    "%s?curpage=%d&limit=%d" format(uri, _curPage, limit)

  def pagination(
                  uri: String,
                  ulClass: String = "pagination pagination-sm pull-right",
                  curPageClass: String = "active"): Elem = {

    val _first =
      if (numpages > paginglimit)
        <li>
          <a href={pageUri(uri, 1)}>首页</a>
        </li>

    val _prev =
      if (curpage > 1)
        <li>
          <a href={pageUri(uri, curpage - 1)}>上一页</a>
        </li>

    val _next =
      if (curpage < numpages)
        <li>
          <a href={pageUri(uri, curpage + 1)}>下一页</a>
        </li>

    val _last =
      if (numpages > paginglimit)
        <li>
          <a href={pageUri(uri, numpages)}>末页</a>
        </li>

    val _pages =
      (pagingoffset to pagingend).map(idx =>
        if (idx == curpage)
          <li class={curPageClass}>
            <a>
              {idx}
            </a>
          </li>
        else
          <li>
            <a href={pageUri(uri, idx)}>
              {idx}
            </a>
          </li>)

    <ul class={ulClass}>
      {_first}{_prev}{_pages}{_next}{_last}
    </ul>
  }

}

trait PaginationSort {
  def sortby: String

  /**
   * @return desc, asc
   */
  def sorting: String
}
