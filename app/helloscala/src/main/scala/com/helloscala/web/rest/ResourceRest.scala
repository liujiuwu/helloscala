package com.helloscala.web
package rest

import com.helloscala.helper.FileHelpers
import net.liftweb.common.{Full, Loggable}
import net.liftweb.util.Helpers._
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.JsonResponse

object ResourceRest extends RestHelper with Loggable {

  /**
   * 上传
   */
  serve("api" / "upload" prefix {
    case "image" :: UrlDecode(creator) :: Nil Post req =>
      val ret =
        for (
          f <- req.uploadedFiles.headOption
        ) yield {
          val file = s"/upload/${f.fileName}"

          FileHelpers.saveWithContext(file) {
            out =>
              out.write(f.file)
          } match {
            case util.Success(_) =>
              logger.info(s"\n用户：$creator 上传文件：${f.fileName} 保存成功。")

              val title = s"${f.fileName} is ${f.mimeType} length ${f.length}"
              returnJson(file, title)

            case util.Failure(e) =>
              logger.error(s"\n用户：$creator 上传文件: ${f.fileName} 保存失败：${e.toString}。")
              returnJson("", "", e.getLocalizedMessage)
          }
        }

      Full(ret getOrElse returnJson("", "", "不是有效的文件上传请求"))
  })

  /**
   * state除了：SUCCESS为成功以外，其它值都是错误消息并会在客户端界面上显示。
   * @param url 图片地址
   * @param title 图片资源 id
   * @param state 上传状态
   * @return
   */
  @inline
  private def returnJson(url: String, title: String, state: String = "SUCCESS") = {
    import net.liftweb.json.JsonDSL._

    JsonResponse(("url" -> url) ~ ("title" -> title) ~ ("state" -> state))
  }

}
