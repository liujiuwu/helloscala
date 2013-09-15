package yangbajing.util.web

import scala.xml.Node

import net.liftweb.http.{LiftResponse, XmlResponse, LiftRules, InMemoryResponse}
import net.liftweb.http.provider.HTTPCookie
import net.liftweb.json.JsonAST
import net.liftweb.http.js.JsExp
import net.liftweb.util.Helpers

object YCsvResponse {
  def apply(bytes: Array[Byte], filename: String): InMemoryResponse =
    apply(bytes, ("Content-disposition", "attachment;filename=" + Helpers.urlEncode(filename)) :: Nil, Nil)

  def apply(bytes: Array[Byte], headers: List[(String, String)] = Nil, cookies: List[HTTPCookie], encode: String = "UTF-8"): InMemoryResponse =
    InMemoryResponse(bytes, ("Content-Type", "text/csv; charset=" + encode) ::("Content-Length", bytes.length.toString) :: headers, cookies, 200)
}

object YXmlResponse {
  def apply(xml: Node, filename: String): XmlResponse =
    apply(xml, ("Content-disposition", "attachment;filename=" + Helpers.urlEncode(filename)) :: Nil, Nil)

  def apply(xml: Node, headers: List[(String, String)] = Nil, cookies: List[HTTPCookie], encode: String = "UTF-8"): XmlResponse =
    new XmlResponse(xml, 200, "text/xml; charset=" + encode, cookies, headers ::: XmlResponse.addlHeaders)
}

object YJsonResponse {
  def apply(json: JsonAST.JValue, filename: String): YJsonResponse =
    apply(json, ("Content-disposition", "attachment;filename=" + Helpers.urlEncode(filename)) :: Nil, Nil, 200)

  def apply(json: JsonAST.JValue, headers: List[(String, String)] = Nil, cookies: List[HTTPCookie], code: Int, encode: String = "UTF-8"): YJsonResponse =
    new YJsonResponse(new JsExp {
      lazy val toJsCmd = jsonPrinter(JsonAST.render((json)))
    }, headers, cookies, code, encode)

  lazy val jsonPrinter: scala.text.Document => String = LiftRules.jsonOutputConverter.vend
}

case class YJsonResponse(json: JsExp, headers: List[(String, String)], cookies: List[HTTPCookie], code: Int, encode: String) extends LiftResponse {
  def toResponse = {
    val bytes = json.toJsCmd.getBytes(encode)
    InMemoryResponse(bytes, ("Content-Length", bytes.length.toString) ::("Content-Type", "text/json; charset=" + encode) :: headers, cookies, code)
  }
}

object YXlsResponse {
  def apply(bytes: Array[Byte], filename: String): InMemoryResponse =
    apply(bytes, ("Content-disposition", "attachment;filename=" + Helpers.urlEncode(filename)) :: Nil, Nil)

  def apply(bytes: Array[Byte], headers: List[(String, String)] = Nil, cookies: List[HTTPCookie], encode: String = "UTF-8"): InMemoryResponse =
    InMemoryResponse(bytes,
      ("Content-Type", "application/vnd.ms-excel; charset=" + encode) ::
        ("Content-Length", bytes.length.toString) :: headers,
      cookies, 200)
}

object YXlsxResponse {
  def apply(bytes: Array[Byte], filename: String): InMemoryResponse =
    apply(bytes, ("Content-disposition", "attachment;filename=" + Helpers.urlEncode(filename)) :: Nil, Nil)

  def apply(bytes: Array[Byte], headers: List[(String, String)] = Nil, cookies: List[HTTPCookie], encode: String = "UTF-8"): InMemoryResponse =
    InMemoryResponse(bytes,
      ("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=" + encode) ::
        ("Content-Length", bytes.length.toString) :: headers,
      cookies, 200)
}
