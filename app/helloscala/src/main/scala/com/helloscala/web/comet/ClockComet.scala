package com.helloscala.web
package comet

import scala.language.postfixOps

import net.liftweb.http.CometActor
import net.liftweb.util.Helpers._
import net.liftweb.util.Schedule

import yangbajing.util.Imports.Y
import com.helloscala.common.Tick

class ClockComet extends CometActor {
  Schedule.schedule(this, Tick, 5 seconds)

  def render =
    "#clock-time *" #> Y.curDateTimeString()

  override def lowPriority = {
    case Tick =>
      val cmds = $("#clock-time").html(Y.curDateTimeString()).cmd
      partialUpdate(cmds)
      Schedule.schedule(this, "Tick", 5 seconds)
  }
}
