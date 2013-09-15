package com.helloscala.web

import com.helloscala.web.rest.ResourceRest
import net.liftweb.http.LiftRules

object HelloRules {
  def init() {
    LiftRules.addToPackages("com.helloscala.web")
    LiftRules.statelessDispatch.append(ResourceRest)
  }
}
