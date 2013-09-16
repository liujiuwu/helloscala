package com.helloscala.web.comet

import scala.language.postfixOps

import net.liftweb.http.{CometActor}
import net.liftweb.http.js.JsCmds
import net.liftweb.util.Helpers._
import net.liftweb.util.Schedule

import yangbajing.util.Imports.Y
import com.helloscala.common.Tick

class MicroChatComet extends CometActor {

  def render =
    "#clock-time *" #> Y.curDateTimeString()

  override def lowPriority = {
    case Tick =>
  }

  override protected def localSetup() {
    super.localSetup()
  }

  override protected def localShutdown() {
    super.localShutdown()
  }
}
