package yangbajing.persistence

import scala.language.implicitConversions
import java.sql.Timestamp
import org.joda.time._
import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.internals.{StatementWriter, OutMapper}
import org.squeryl.dsl.ast.{ExpressionNode, FunctionNode}

object SquerylEntrypoint extends SquerylEntrypoint

trait SquerylEntrypoint extends PrimitiveTypeMode with MyJodaTime with MyLocalDate with CustomFunction

//trait MyPostgres {
//  this: PrimitiveTypeMode =>
//
//  class MyCrypto(e: StringExpression[String], m: OutMapper[String])
//    extends FunctionNode[String]("sha1", Some(m), Seq(e)) with StringExpression[String]
//
//  def crypto(e: StringExpression[String])(implicit m: OutMapper[String]) = new MyCrypto(e, m)
//
//}

trait CustomFunction {
  this: PrimitiveTypeMode =>

  //  def crypt(e: ExpressionNode[String], salt: ExpressionNode[String]) =
  //    new PGCrypt(e, salt)

  //  def gen_salt(e: ExpressionNode[String]) =
  //    new GenSalt(e)

  //  class PGCrypt(e: ExpressionNode[String], salt: ExpressionNode[String])
  //    extends FunctionNode[String]("crypt", Seq(e, salt)) with ExpressionNode[String]

  //  class GenSalt(salt: ExpressionNode[String])
  //    extends FunctionNode[String]("gen_salt", Seq(e)) with ExpressionNode[String]

//  def crypt[A1, T1](s: TypedExpression[A1, T1], column: String)(implicit f: TypedExpressionFactory[A1, T1], ev2: T1 <:< TOptionString) =
//    f.convert(new CryptNode("crypt", s, column))
//
//  def gen_salt[A1, T1](s: TypedExpression[A1, T1])(implicit f: TypedExpressionFactory[A1, T1], ev2: T1 <:< TOptionString) =
//    f.convert(new FunctionNode("gen_salt", Seq(s)))
//
//  protected class CryptNode(val name: String, val s: ExpressionNode, val column: String) extends ExpressionNode {
//
//    def doWrite(sw: StatementWriter) = {
//
//      sw.write(name)
//      sw.write("(")
//      s.write(sw)
//      sw.write(", \"")
//      sw.write(column)
//      sw.write("\")")
//    }
//
//    override def children = List(s, column)
//  }

}

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

}

trait MyLocalDate {
  this: PrimitiveTypeMode =>

  implicit val localDateTEF = new NonPrimitiveJdbcMapper[java.util.Date, LocalDate, TDate](dateTEF, this) {
    def convertFromJdbc(t: java.util.Date) = new LocalDate(t)

    def convertToJdbc(t: LocalDate) = t.toDate
  }

  implicit val optionLocalDateTEF =
    new TypedExpressionFactory[Option[LocalDate], TOptionDate] with DeOptionizer[java.util.Date, LocalDate, TDate, Option[LocalDate], TOptionDate] {
      val deOptionizer = localDateTEF
    }

  implicit def localDateToTE(s: LocalDate) = localDateTEF.create(s)

  implicit def optionLocalDateToTE(s: Option[LocalDate]) = optionLocalDateTEF.create(s)

}
