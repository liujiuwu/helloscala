package yangbajing.util.data

import org.apache.poi.ss.usermodel.{Sheet, Workbook}
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import yangbajing.util.Imports.Y

//import yangbajing.common.EnumHelper

object PoiTypes {

  object TestCode extends Enumeration {
    type TestCode = Value

    val Xls = Value(1, "xls")
    val Xlsx = Value(2, "xlsx")

    lazy val opts = values.toList.map(v => v.id.toString -> v.toString)

    lazy val objOpts = values.toList.map(v => v -> v.toString)

    def option(v: Any) =
      Y tryoption (v match {
        case i: Int =>
          apply(i)

        case s: String =>
          Y tryoption apply(s.toInt) getOrElse withName(s)

        case _ =>
          throw new IllegalArgumentException
      })
  }

  object PoiCode extends Enumeration /*with EnumHelper[PoiCode.type] */ {
    type PoiCode = Value

    def self = this

    val Xls = Value(1, "xls")
    val Xlsx = Value(2, "xlsx")

    /////////////////////////////////////////////////////////////////////
    // @ TODO 使用 reflect api 重构代码到trait中
    /////////////////////////////////////////////////////////////////////
    lazy val opts = values.toList.map(v => v.id.toString -> v.toString)

    lazy val objOpts = values.toList.map(v => v -> v.toString)

    def option(v: Any) =
      Y tryoption (v match {
        case i: Int =>
          apply(i)

        case s: String =>
          Y tryoption apply(s.toInt) getOrElse withName(s)

        case _ =>
          throw new IllegalArgumentException
      })

    /////////////////////////////////////////////////////////////////////
    // # TODO 使用 reflect api 重构代码到trait中
    /////////////////////////////////////////////////////////////////////

  }

}

object PoiHelper {

  def xls(headers: Seq[DataLabel], tables: Seq[Map[String, Any]], sheetName: String = "Sheet 1"): XSSFWorkbook = {
    val table = new PoiHelper(headers, tables, new XSSFWorkbook(), Seq(sheetName))
    table.execute.wb
  }

  def xlsx(headers: Seq[DataLabel], tables: Seq[Map[String, Any]], sheetName: String = "Sheet 1"): HSSFWorkbook = {
    val table = new PoiHelper(headers, tables, new HSSFWorkbook(), Seq(sheetName))
    table.execute.wb
  }

  def xlsSheet(wb: XSSFWorkbook, headers: Seq[DataLabel], tables: Seq[Map[String, Any]], sheetName: String = "Sheet 1") = {
    new PoiSheet(wb, headers, tables, sheetName).execute
  }

  def xlsxSheet(wb: HSSFWorkbook, headers: Seq[DataLabel], tables: Seq[Map[String, Any]], sheetName: String = "Sheet 1") =
    new PoiSheet(wb, headers, tables, sheetName).execute

  def toXls(headers: Seq[(String, String)], tables: Seq[Map[String, String]], sheetName: String = "Sheet 1"): XSSFWorkbook = {
    val table = new PoiTable(headers, tables, new XSSFWorkbook(), Seq(sheetName))
    table.execute.wb
  }

  def toXlsx(headers: Seq[(String, String)], tables: Seq[Map[String, String]], sheetName: String = "Sheet 1"): HSSFWorkbook = {
    val table = new PoiTable(headers, tables, new HSSFWorkbook(), Seq(sheetName))
    table.execute.wb
  }

  class PoiTable[T <: Workbook](
                                 header: Seq[(String, String)],
                                 tables: Seq[Map[String, String]],
                                 val wb: T,
                                 sheetNames: Seq[String]) extends TraitPoi {

    def execute = {
      val sheet = wb.createSheet(sheetNames.head)
      val rowTitle = sheet.createRow(0)

      for (((key, value), col) <- header.zipWithIndex) {
        rowTitle.createCell(col).setCellValue(value)
      }

      for ((map, rowIdx) <- tables.zipWithIndex) {
        val row = sheet.createRow(rowIdx + 1)
        for (((key, _), col) <- header.zipWithIndex)
          createCellValue(row, col, DataGenre.TEXT, map.getOrElse(key, ""))
      }

      this
    }

  }

}

private class PoiSheet[T <: Workbook](
                                       val wb: T,
                                       header: Seq[DataLabel],
                                       tables: Seq[Map[String, Any]],
                                       sheetName: String) extends TraitPoi {

  def execute: Sheet = {
    val sheet = wb.createSheet(sheetName)
    val rowTitle = sheet.createRow(0)

    for ((data, col) <- header.zipWithIndex) {
      rowTitle.createCell(col).setCellValue(data.label getOrElse data.name)
    }

    for ((map, idx) <- tables.zipWithIndex) {
      val row = sheet.createRow(idx + 1)

      for ((data, col) <- header.zipWithIndex)
        map.get(data.name) match {
          case Some(value) =>
            createCellValue(row, col, data.genre, value)
          case None =>
            createCell(row, col)
        }
    }

    sheet
  }

}


private class PoiHelper[T <: Workbook](
                                        header: Seq[DataLabel],
                                        tables: Seq[Map[String, Any]],
                                        val wb: T,
                                        sheetNames: Seq[String]) extends TraitPoi {

  def execute = {
    val sheet = wb.createSheet(sheetNames.head)
    val rowTitle = sheet.createRow(0)

    for ((data, col) <- header.zipWithIndex) {
      rowTitle.createCell(col).setCellValue(data.label getOrElse data.name)
    }

    for ((map, idx) <- tables.zipWithIndex) {
      val row = sheet.createRow(idx + 1)

      for ((data, col) <- header.zipWithIndex)
        map.get(data.name) match {
          case Some(value) =>
            createCellValue(row, col, data.genre, value)
          case None =>
            createCell(row, col)
        }
    }

    this
  }

}


