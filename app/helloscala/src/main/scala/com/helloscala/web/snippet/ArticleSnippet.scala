package com.helloscala.web
package snippet

import scala.xml.NodeSeq

import net.liftweb.http.{Templates, SHtml, S, DispatchSnippet}
import net.liftweb.util.Helpers._
import net.liftweb.util.{CssSel, ClearNodes}
import net.liftweb.common.{Loggable, Failure, Full}
import net.liftweb.http.js.{JsCmds, JsCmd}

import org.joda.time.DateTime

import yangbajing.util.Imports.Y
import yangbajing.util.CompareCode

import com.helloscala.helper.HelloHelpers
import com.helloscala.model.{MComment, Account, MUser, MArticle}
import com.helloscala.service.HelloSystem
import com.helloscala.common.ArticleTags

object ArticleSnippet extends DispatchSnippet with Loggable {
  def dispatch = {
    case "panel" => panel
    case "show" => show
    case "comments" => comments
    case "comment" => comment
    case "edit" => edit
  }

  def panel(nodeSeq: NodeSeq): NodeSeq = {
    val panelClass = S.attr("panelClass") or H.param("panelClass") openOr ""
    val tpe = S.param("tpe") or S.attr("tpe") openOr ""
    val limit = S.param("limit") or S.attr("limit") flatMap (asInt) openOr H.defaultLimit
    val id = H.paramLong("article_id")
    val compare = H.paramInt("compare").flatMap(v => Y.tryBox(CompareCode(v))).openOr(CompareCode.Lte)

    val pager = MArticle.pager(limit, id, compare)

    val articleNewSel =
      W.theAccount.is match {
        case Full(account) => "@article-new [href]" #> s"/u/${account.id}/article/new"
        case _ => "@article-new" #> ClearNodes
      }

    val articlesSel =
      "li" #> pager.page.map {
        article =>
          val userHref = s"/u/${article.author}"
          val imgSrc = "/assets/img/logo-1-32.png" // s"/upload/user/${article.author}/avatar-32.png"
        val articleHref = s"/article/${article.id}"
          val meta =
            <span>
              <a href={userHref}>
                {article.author}
              </a>{HelloHelpers.timeDescription(article.createdAt)}
            </span>

          //            ".badge" #> ClearNodes &
          ".article-avatar" #> (
            "a [href]" #> userHref &
              "img [src]" #> imgSrc
            ) &
            ".article-info" #> (
              "a [href]" #> articleHref &
                "a *" #> article.title &
                ".text-muted" #> meta
              )
      }

    val cssSel =
      ".panel [class+]" #> panelClass &
        ".panel-title -*" #> panelTitle(tpe) &
        articleNewSel &
        ".articles" #> articlesSel

    cssSel(nodeSeq)
  }

  def edit(nodeSeq: NodeSeq): NodeSeq =
    W.theAccount.is match {
      case Full(account) if W.idEquals(W.uriSpace) => _edit(account) apply nodeSeq
      case _ => <h1 class="text-danger">没有编辑权限</h1>
    }

  private[this] def _edit(account: Account): CssSel = {
    val isAdd = W.reqArticle.is.isEmpty

    val req = W.reqArticle.is match {
      case Full(a) => new ReqArticle(a.author, a.tags, a.title, a.content, Some(a.createdAt))
      case _ => new ReqArticle(author = account.id)
    }

    def submitFunc(): JsCmd = {
      val article = MArticle(req.author, req.title, req.content)
      if (isAdd)
        MArticle.create(article) match {
          case Full(a) =>
            HelloSystem.tagActor ! ArticleTags(a.id, req.newTags, req.tags) // 保存成功，更新tag
            JsCmds.Alert("创建文章成功") & JsCmds.RedirectTo(s"${a.id}")
          case Failure(msg, e, _) =>
            val errorMsg = e.map(_.getLocalizedMessage) openOr msg
            e foreach (logger debug _.getLocalizedMessage)
            JsCmds.Alert(errorMsg)
          case _ =>
            JsCmds.Alert("创建文件失败！")
        }
      else
        MArticle.save(article) match {
          case Full(a) =>
            HelloSystem.tagActor ! ArticleTags(a.id, req.newTags, req.tags) // 保存成功，更新tag
            JsCmds.Alert("保存文章成功") & JsCmds.RedirectTo(s"${a.id}")
          case Failure(msg, e, _) =>
            val errorMsg = e.map(_.getLocalizedMessage) openOr msg
            e foreach (logger debug _.getLocalizedMessage)
            JsCmds.Alert(errorMsg)
          case _ =>
            JsCmds.Alert("保存文件失败！")
        }
    }

    ".panel-title -*" #> (if (isAdd) "创建" else "编辑") &
      "@title" #> SHtml.text(req.title, req.title = _) &
      "@content" #> SHtml.textarea(req.content, req.content = _) &
      "@tags" #> SHtml.text(req.tags.mkString(","), v => req.newTags = v.split(',').toList.map(_.trim)) &
      ":submit *" #> (if (isAdd) "立即创建" else "保存修改") &
      "@submit" #> SHtml.hidden(submitFunc)
  }

  def show(nodeSeq: NodeSeq): NodeSeq = {
    val cssSel =
      W.reqArticle.is match {
        case Full(article) =>
          val labelSel =
            if (article.tags.isEmpty) ".label-item" #> ClearNodes
            else ".label" #> article.tags.map(t => ".label *" #> t)

          ".panel-heading" #> (
            ".panel-title *" #> article.title &
              (W.theAccount.is match {
                case Full(account) => "a [href]" #> s"/u/${article.author}/article/${article.id}/edit"
                case _ => "a" #> ClearNodes
              })) &
            labelSel &
            ".content *" #> article.content

        case _ =>
          "*" #> <h1 class="text-danger">文章不存在</h1>
      }

    cssSel(nodeSeq)
  }

  def comments(nodeSeq: NodeSeq): NodeSeq = {
    val cssSel =
      for (article <- W.reqArticle.is)
      yield {
        "@comment-size *" #> MArticle.commentSize(article.id) &
          ".comments" #> (
            "li" #> article.comments.map(c => _commentLi(c)))
      }

    cssSel map (_ apply nodeSeq) openOr NodeSeq.Empty
  }

  def comment(nodeSeq: NodeSeq): NodeSeq = {
    val cssSel =
      for (
        article <- W.reqArticle.is;
        account <- W.theAccount.is
      ) yield {
        val articleId = article.id
        var comment = ""
        val creator = account.id

        "@comment" #> SHtml.textarea(comment, comment = _) &
          "@submit" #> SHtml.hidden(() =>
            MArticle.createComment(articleId, creator, comment) match {
              case Full(c) =>
                $("#article-comments").prepared(_commentLi(c)).cmd & JsCmds.Alert("成功发表评论")

              case _ =>
                JsCmds.Alert("评论失败")
            })
      }

    cssSel map (_ apply nodeSeq) openOr NodeSeq.Empty
  }

  private[this] def _commentLi(c: MComment): NodeSeq = {
    val avatarUri = s"/u/${MUser.findOne(c.creator).flatMap(_.spaceName) openOr c.creator}"

    val cssSel =
      ".badge" #> ClearNodes &
        ".comment-avatar" #> (
          "a [href]" #> avatarUri //&
          //            "img [src]" #> HelloHelpers.avatarUri(c.creator)
          ) &
        ".comment-info" #> (
          "p *" #> c.content &
            ".text-muted *" #> HelloHelpers.timeDescription(c.createdAt)
          )

    cssSel(nodeCommentLi)
  }

  private[this] def panelTitle(tpe: String): String =
    tpe match {
      case "new" => "最近"
      case "popular" => "推荐"
      case _ => ""
    }

  def nodeCommentLi = Templates(List("page", "article", "_comment_li")).openOrThrowException("/page/article/_comment_li not found!!!")
}

class ReqArticle(var author: String = "",
                 val tags: List[String] = Nil,
                 var title: String = "",
                 var content: String = "",
                 createdAt: Option[DateTime] = None) {
  var newTags: List[String] = Nil
}
