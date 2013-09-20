package com.helloscala.web

import net.liftweb.common.Loggable
import net.liftweb.http._
import net.liftweb.sitemap.{**, Menu, SiteMap}
import net.liftweb.sitemap.Loc.Stateless
import net.liftweb.util.Helpers._

import com.helloscala.web.rest.ResourceRest
import com.helloscala.helper.RdbHelpers
import com.helloscala.service.HelloSystem

object HelloRules extends Loggable {

  def init() {
    LiftRules.addToPackages("com.helloscala.web")
    LiftRules.statelessDispatch.append(ResourceRest)
    LiftRules.setSiteMap(sitemap)
    LiftRules.statelessRewrite.prepend(uriRewrites)

    LiftRules.unloadHooks.append(() => HelloSystem.system.shutdown())
    RdbHelpers.registerSquerylSession()
    RdbHelpers.close()
  }

  def sitemap() =
    SiteMap(
      Menu("assets") / "assets" / ** >> Stateless,
      Menu("page") / "page" / **,
      Menu("demo") / "demo" / **,
      Menu("static") / "static" / **,
      Menu("c") / "c" submenus(
        Menu("c-index") / "c" / "index" >> W.testSession,
        Menu("c-sign-in") / "c" / "sign_in",
        Menu("c-sign-up") / "c" / "sign_up",
        Menu("c-sign-out") / "c" / "sign_out" >> W.signOut),
      Menu("index") / "index",
      Menu("support") / "support" / **
    )

  val uriRewrites: LiftRules.RewritePF = {
    case RewriteRequest(ParsePath("article" :: subs, suffix, true, _), _, _) =>
      subs match {
        case Nil =>
          RewriteResponse(List("page", "article", "index"))

        case AsLong(articleId) :: Nil =>
          RewriteResponse(List("page", "article", "show"), Map("article_id" -> articleId.toString))
      }

    case RewriteRequest(ParsePath("u" :: userId :: subs, suffix, true, _), _, _) =>
      logger.debug(s"\nParsePath: userId: $userId " + subs)

      subs match {
        case Nil =>
          RewriteResponse(List("page", "user", "detail"), Map("user_id" -> userId))

        case "article" :: "new" :: Nil =>
          RewriteResponse(List("page", "article", "edit"))

        case "article" :: AsLong(articleId) :: "edit" :: Nil =>
          RewriteResponse(List("page", "article", "edit"), Map("article_id" -> articleId.toString))

        case "article" :: AsLong(articleId) :: Nil =>
          RewriteResponse(List("page", "article", "show"), Map("article_id" -> articleId.toString))

        case "micro_chat" :: id :: Nil =>
          RewriteResponse(List("page", "micro_chat", "detail"), Map("user_id" -> userId, "micro_chat_id" -> id))

        case list =>
          RewriteResponse("page" :: "user" :: list, Map("user_id" -> userId))
      }

    case RewriteRequest(ParsePath("u" :: Nil, suffix, true, _), _, _) =>
      RewriteResponse(List("page", "user", "index"))
  }

}
