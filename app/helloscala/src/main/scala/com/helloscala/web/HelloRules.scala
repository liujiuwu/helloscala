package com.helloscala.web

import net.liftweb.http.{RewriteResponse, ParsePath, RewriteRequest, LiftRules}
import net.liftweb.sitemap.{**, Menu, SiteMap}
import net.liftweb.sitemap.Loc.Stateless

import com.helloscala.web.rest.ResourceRest
import com.helloscala.helper.RdbHelpers

object HelloRules {

  def init() {
    LiftRules.addToPackages("com.helloscala.web")
    LiftRules.statelessDispatch.append(ResourceRest)
    LiftRules.setSiteMap(sitemap)
    LiftRules.statelessRewrite.prepend(uriRewrites)

    RdbHelpers.registerSquerylSession()
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
    case RewriteRequest(ParsePath("u" :: userId :: subs, suffix, true, _), _, _) =>
      subs match {
        case "micro_chat" :: id :: Nil =>
          RewriteResponse(List("page", "micro_chat", "detail"), Map("user_id" -> userId, "micro_chat_id" -> id))

        case "blog" :: category :: Nil =>
          RewriteResponse(List("page", "blog", "index"), Map("user_id" -> userId, "category" -> category))

        case "blog" :: category :: id :: Nil =>
          RewriteResponse(List("page", "blog", "detail"), Map("user_id" -> userId, "category" -> category, "id" -> id))

        case Nil =>
          RewriteResponse(List("page", "account", "index"), Map("user_id" -> userId))

        case list =>
          RewriteResponse("page" :: "account" :: list, Map("user_id" -> userId))
      }
  }

}
