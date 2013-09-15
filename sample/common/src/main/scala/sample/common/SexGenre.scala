package sample.common

object SexGenre extends Enumeration {
  val Male = Value(1, "男性")
  val Female = Value("女性")
  val Neutral = Value("中性")

  val opts = values.map(v => v.id.toString -> v.toString)
}
