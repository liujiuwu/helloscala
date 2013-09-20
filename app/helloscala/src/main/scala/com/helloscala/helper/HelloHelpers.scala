package com.helloscala.helper

import org.joda.time._
import scala.Some

object HelloHelpers {
  def avatarUri(id: String): String =
    s"/upload/user/${id}/avatar-32.png"

  def timeDescription(at: DateTime): String = {
    val now = DateTime.now()

    yearsBetween(at, now).map(i => s"发布于${i}年前发布") orElse
      monthsBetween(at, now).map(i => s"发布于${i}个月前发布") orElse
      weeksBetween(at, now).map(i => s"发布于${i}周前发布") orElse
      daysBetween(at, now).map(i => s"发布于${i}天前发布") getOrElse
      "刚发布"
  }

  @inline
  def yearsBetween(at: DateTime, now: DateTime): Option[Int] =
    Years.yearsBetween(at, now).getYears match {
      case i if i <= 0 => None
      case i => Some(i)
    }

  @inline
  def monthsBetween(at: DateTime, now: DateTime): Option[Int] =
    Months.monthsBetween(at, now).getMonths match {
      case i if i <= 0 => None
      case i => Some(i)
    }

  @inline
  def weeksBetween(at: DateTime, now: DateTime): Option[Int] =
    Weeks.weeksBetween(at, now).getWeeks match {
      case i if i <= 0 => None
      case i => Some(i)
    }

  @inline
  def daysBetween(at: DateTime, now: DateTime): Option[Int] =
    Days.daysBetween(at, now).getDays match {
      case i if i <= 0 => None
      case i => Some(i)
    }


  def tags: List[String] = synchronized(_tags)

  def tags_=(vs: List[String]): Unit = synchronized(_tags = vs)

  @volatile private[this] var _tags: List[String] = Nil

}
