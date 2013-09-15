package sample.squeryl.basic.helper

import scala.language.implicitConversions

import java.sql.Timestamp
import org.joda.time._

import org.squeryl._
import org.squeryl.dsl._

object SquerylEntrypoint extends SquerylEntrypoint

trait SquerylEntrypoint extends PrimitiveTypeMode with MyJodaTime

trait MyJodaTime {
  this: PrimitiveTypeMode =>

  implicit val jodaTimeTEF = new NonPrimitiveJdbcMapper[Timestamp, DateTime, TTimestamp](timestampTEF, this) {

    def convertFromJdbc(t: Timestamp) = new DateTime(t)

    def convertToJdbc(t: DateTime) = new Timestamp(t.getMillis())
  }

  implicit val optionJodaTimeTEF =
    new TypedExpressionFactory[Option[DateTime], TOptionTimestamp] with DeOptionizer[Timestamp, DateTime, TTimestamp, Option[DateTime], TOptionTimestamp] {

      val deOptionizer = jodaTimeTEF
    }

  implicit def jodaTimeToTE(s: DateTime) = jodaTimeTEF.create(s)

  implicit def optionJodaTimeToTE(s: Option[DateTime]) = optionJodaTimeTEF.create(s)

  implicit val localDateTEF = new NonPrimitiveJdbcMapper[java.util.Date, LocalDate, TDate](dateTEF, this) {
    def convertFromJdbc(t: java.util.Date) = new LocalDate(t)

    def convertToJdbc(t: LocalDate) = t.toDateMidnight.toDate
  }

  implicit val optionLocalDateTEF =
    new TypedExpressionFactory[Option[LocalDate], TOptionDate] with DeOptionizer[java.util.Date, LocalDate, TDate, Option[LocalDate], TOptionDate] {
      val deOptionizer = localDateTEF
    }

  implicit def localDateToTE(s: LocalDate) = localDateTEF.create(s)

  implicit def optionLocalDateToTE(s: Option[LocalDate]) = optionLocalDateTEF.create(s)

}
