package yangbajing.log

object LogTypes {
  object Level extends Enumeration {
    type Level = Value

    val ERROR = Value(100)
    val WARN = Value(200)
    val IMPORTANT = Value(300)
    val INFO = Value(400)
    val DEBUG = Value(500)
    val TRACE = Value(600)
  }
}
