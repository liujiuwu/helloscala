package yangbajing.util.data

import com.novus.salat.annotations._
import com.novus.salat.EnumStrategy
import yangbajing.util.Imports.Y

/**
 * 数据类型
 */
@EnumAs(strategy = EnumStrategy.BY_VALUE)
object DataGenre extends Enumeration {
  val TEXT = Value(1, "text")
  val INT = Value("int")

  // long, bigint
  val LONG = Value("long")

  // double, float, bigdecimal
  val NUMBER = Value("number")

  val DATETIME = Value("datetime")
  val DATE = Value("date")
  val TIME = Value("time")

  val TEXT_ARRAY = Value("text_array")
  val INT_ARRAY = Value("int_array")
  val LONG_ARRAY = Value("long_array")
  val NUMBER_ARRAY = Value("number_array")
  val DATETIME_ARRAY = Value("datetime_array")
  val DATE_ARRAY = Value("date_array")
  val TIME_ARRAY = Value("time_array")

  val FORMULA = Value("formula")
  val ENUM = Value("enum")

  def parse(any: Any): Option[DataGenre.Value] =
    Y.tryoption(any match {
      case i: Int =>
        apply(i)
      case s: String =>
        withName(s)
    })
}

/**
 * @param name
 * @param genre
 * @param label
 */
case class DataLabel(
                      name: String,
                      genre: DataGenre.Value,
                      label: Option[String] = None)
