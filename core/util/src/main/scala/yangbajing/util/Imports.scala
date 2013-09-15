package yangbajing.util

import scala.util.Try
import yangbajing.common.BaseImplicitly

object Imports extends BaseImplicitly {

  implicit class YEitherToBox[T](value: Either[_ <: Throwable, T]) {
    def toBox = Y.toBox(value)
  }

  implicit class YTryToBox[T](value: Try[T]) {
    def toBox = Y.toBox(value)
  }

  //  @inline def trybox[R](func: => R) = Y trybox func
  //
  //  @inline def dtrybox[R](msg: String)(func: => R) = Y.dtrybox(msg)(func)


  object Y extends Y {

    import org.json4s._
    import org.json4s.native._
    import org.json4s.ext.JodaTimeSerializers

    implicit val defaultFormats = DefaultFormats ++ JodaTimeSerializers.all

    def json4sToJValue(s: String): JValue =
      JsonParser.parse(s)

    def json4sToString(v: JValue): String =
      Serialization.write(v)

    //    def json4sToString[T <: Product](v: T): String =
    //      Serialization.write(v)
  }

}
