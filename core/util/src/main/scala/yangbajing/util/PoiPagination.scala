package yangbajing.util

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbook

import yangbajing.util.data.{DataLabel, PoiHelper}

trait PoiPagination[T <: ExportLike] extends Pagination[T] {
  def xls: XSSFWorkbook =
    PoiHelper.xls(header, page.map(_.toMap), s"${curpage}-${limit}")

  def xlsx: HSSFWorkbook =
    PoiHelper.xlsx(header, page.map(_.toMap), s"${curpage}-${limit}")

  def header: Seq[DataLabel]
}


