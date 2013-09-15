package yangbajing.util

import scala.util.{Try, Success => SSuccess, Failure => SFailure}
import scala.collection.GenTraversableOnce

import net.liftweb.common.{Box, Empty, Full, Failure}

import yangbajing.common.{BaseImplicitly, BaseY}

trait Y extends BaseY with BaseRandom with BaseImplicitly {
  def dtrybox[R](msg: String)(func: => R) = try {
    Box !! func
  } catch {
    case e: Exception =>
      //      e.printStackTrace
      Failure(msg, Full(e), Empty)
  }

  def option[T <: CharSequence](b: Box[T]): Option[T] =
    b.filterNot(_.length == 0).toOption

  def optionGen[T <: GenTraversableOnce[_]](b: Box[T]): Option[T] =
    b.filterNot(_.isEmpty).toOption

  def box[T <: CharSequence](v: T): Box[T] =
    Box !! v filterNot (_.length == 0)

  def boxGen[T <: GenTraversableOnce[_]](v: T): Box[T] =
    Box !! v filterNot (_.isEmpty)

  def trybox[R](func: => R) = try {
    val r = func
    Box !! r
  } catch {
    case e: Exception =>
      //      e.printStackTrace
      Failure(e.getMessage, Full(e), Empty)
  }

  def toBox[E <: Throwable, T](either: Either[E, T]): Box[T] = either match {
    case Left(v) => Failure("", Full(v), Empty)
    case Right(v) => Full(v)
  }

  def toBox[T](value: Try[T]): Box[T] = value match {
    case SSuccess(v) => Full(v)
    case SFailure(v) => Failure(v.getMessage, Full(v), Empty)
  }
}
