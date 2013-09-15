package bootstrap.liftweb

import scala.language.postfixOps
import net.liftweb.common.{Loggable, Full}
import net.liftweb.http.LiftRulesMocker.toLiftRules
import net.liftweb.http._
import net.liftweb.util.Helpers._
import net.liftweb.util.{NamedPF, Props}
import com.helloscala.web.HelloRules

class Boot extends Loggable {

  def boot() {
    // Ajax 调用时显示图像
    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // 设置HTML渲染使用HTML5模式
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // 上传文件保存到临时文件
    //LiftRules.handleMimeFile = OnDiskFileParamHolder.apply

    // 30秒后开始淡出，再15秒后实际退出
    LiftRules.noticesAutoFadeOut.default.set((notices: NoticeType.Value) => Full(30.seconds -> 15.seconds))

    println("\n\nLiftweb run mode: " + Props.mode)

    Props.mode match {
      case Props.RunModes.Production =>
        LiftRules.exceptionHandler.prepend {
          case (runMode, request, exception) =>
            logger.error("Boom! At %s" format request.uri, exception)
            InternalServerErrorResponse()
        }

      case _ =>
    }

    // 自定义404页面
    LiftRules.uriNotFound.prepend(NamedPF("404handler") {
      case (req, failure) =>
        NotFoundAsTemplate(ParsePath(List("404"), "html", absolute = false, endSlash = false))
    })

    LiftRules.templateSuffixes ::= "xml"

    // 为Rest添加已知后缀
    LiftRules.explicitlyParsedSuffixes ++=
      Seq("csv", "xlsx", "docx", "pptx", "scala", "clj", "md", "markdown", "sql", "C")

    HelloRules.init()
  }

}
