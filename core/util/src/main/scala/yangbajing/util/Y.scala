package yangbajing.util

import scala.util.{Try, Success => SSuccess, Failure => SFailure}
import scala.collection.GenTraversableOnce

import net.liftweb.common.{Box, Empty, Full, Failure}

import yangbajing.common.{BaseImplicitly, BaseY}
import net.liftweb.http.provider.servlet.HTTPServletContext
import net.liftweb.http.LiftRules
import java.io.{FileOutputStream, OutputStream, File}

trait Y extends BaseY with BaseRandom with BaseImplicitly {

  /**
   * 获取文件本地绝对路径
   * @param f 相对站点根路径，必需以 / 开头
   * @return
   */
  def realPath(f: String): Option[String] =
    Box.asA[HTTPServletContext](LiftRules.context).map(_.ctx.getRealPath(f)).filterNot(_ eq null).toOption

  /**
   * 获取文件本地绝对路径
   * @param f 相对站点根路径，必需以 / 开头
   * @return 成功返回Right(File对象)，失败返回Left(错误消息)
   */
  @inline
  def realFile(f: String): Either[String, File] =
    realPath(f).map(new File(_)) match {
      case Some(file) => mkFile(file)
      case None => Left(s"$f 不能获取文件的本地磁盘实际路径，请检查文件名是否符合系统要求！")
    }


  /**
   * 判断文件是否存在，不存在则创建它
   * @param f 文件本地全路径
   * @return 成功返回Right(File对象)，失败返回Left(错误消息)
   */
  @inline
  def mkFile(f: String): Either[String, File] =
    mkFile(new File(f))

  /**
   * 判断文件是否存在，不存在则创建它
   * @param f 文件
   * @return 成功返回Right(File对象)，失败返回Left(错误消息)
   */
  def mkFile(f: File): Either[String, File] =
    f match {
      case file if file.exists() =>
        Right(file)

      case path if path.isDirectory =>
        if (path.mkdirs()) Right(path)
        else Left(s"创建$path 失败")

      case file =>
        val path = file.getParentFile

        if (path.exists()) Right(file)
        else if (path.mkdirs()) Right(file)
        else Left(s"创建$file 所在目录失败")
    }

  /**
   * 保存文件到本地磁盘
   * @param f 相对站点根目录路径（注：必需以 / 开头）
   * @param func
   * @tparam R
   * @return
   */
  def saveWithContext[R](f: String)(func: OutputStream => R): Try[R] =
    Try(realFile(f) match {
      case Right(file) =>
        var out: FileOutputStream = null

        try {
          out = new FileOutputStream(file)
          func(out)
        } finally
          if (out ne null) out.close()

      case Left(msg) =>
        throw new IllegalAccessException(msg)
    })

  /**
   * 保存文件到本地磁盘
   * @param f
   * @param func
   * @tparam R
   * @return
   */
  def saveWithFile[R](f: File)(func: OutputStream => R): Try[R] =
    Try(mkFile(f) match {
      case Right(file) =>
        var out: FileOutputStream = null

        try {
          out = new FileOutputStream(file)
          func(out)
        } finally
          if (out ne null) out.close()

      case Left(msg) =>
        throw new IllegalAccessException(msg)
    })

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

  def tryBox[R](func: => R): Box[R] = try {
    func match {
      case r: Box[R] => r
      case r => Box !! r
    }
  } catch {
    case e: Exception =>
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
