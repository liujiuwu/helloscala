package com.helloscala.common

trait EnumTrait {
  this: Enumeration =>

  lazy val opts = values.map(v => v.id.toString -> v.toString).toList

  lazy val optObjs = values.map(v => v -> v.toString).toList

  lazy val optIds = values.map(v => v.id -> v.toString).toList
}
