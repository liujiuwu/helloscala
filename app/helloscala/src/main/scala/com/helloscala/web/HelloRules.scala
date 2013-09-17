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
    // Blog
    case RewriteRequest(ParsePath("u" :: UrlDecode(account) :: "blog" :: subs, suffix, true, _), _, _) =>
      val paths = "page" :: "blog" :: subs
      val params = Map("account" -> account)
      RewriteResponse(paths, params)

    // Account center
    case RewriteRequest(ParsePath("u" :: UrlDecode(account) :: subs, suffix, true, _), _, _) =>
      val paths = "page" :: "account" :: subs
      val params = Map("account" -> account)
      RewriteResponse(paths, params)
  }
}
