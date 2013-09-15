package com.helloscala.common

object SexCode extends Enumeration with EnumTrait {
  val Male = Value(1, "男性")
  val Female = Value("女性")
  val Neutral = Value("中性") // XXX 人妖 ^_^
}
