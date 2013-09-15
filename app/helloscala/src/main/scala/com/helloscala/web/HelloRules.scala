package com.helloscala.web

import com.helloscala.web.rest.ResourceRest
import net.liftweb.http.LiftRules
import net.liftweb.sitemap.{**, Menu, SiteMap}
import net.liftweb.sitemap.Loc.Stateless
import com.helloscala.helper.RdbHelpers

object HelloRules {

  def sitemap() =
    SiteMap(
      Menu("assets") / "assets" / ** >> Stateless,
      Menu("_page") / "_page" / **,
      Menu("static") / "static" / **,
      Menu("c") / "c" submenus(
        Menu("c-index") / "c" / "index" >> W.testSession,
        Menu("c-sign-in") / "c" / "sign_in",
        Menu("c-sign-up") / "c" / "sign_up",
        Menu("c-sign-out") / "c" / "sign_out" >> W.signOut),
      Menu("index") / "index",
      Menu("support") / "supoort" / **
    )

  def init() {
    LiftRules.addToPackages("com.helloscala.web")
    LiftRules.statelessDispatch.append(ResourceRest)
    LiftRules.setSiteMap(sitemap)

    RdbHelpers.registerSquerylSession()
  }
}
