package yangbajing.common

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import org.joda.time.{LocalTime, LocalDate, DateTime}
import java.io.{PrintWriter, StringWriter}

object BaseY extends BaseY

trait BaseY extends TryUsingResource
with MathHelper
with TimeHelper {
  val perDayMillis = 1000L * 60L * 60L * 24L

  lazy val md5 = DigestHelpers("MD5")
  lazy val sha1 = DigestHelpers("SHA1")
  lazy val sha256 = DigestHelpers("SHA256")
  lazy val sha512 = DigestHelpers("SHA512")

  lazy val emailer = java.util.regex.Pattern.compile(s"\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*")

  lazy val formaterDatetimeWeak = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E")
  lazy val formaterDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  lazy val formaterDatetimeMillis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

  lazy val formaterDate = new SimpleDateFormat("yyyy-MM-dd")
  lazy val formaterMonth = new SimpleDateFormat("yyyy-MM")

  lazy val formaterMinute = new SimpleDateFormat("HH:mm")
  lazy val formaterTime = new SimpleDateFormat("HH:mm:ss")
  lazy val formaterTimeMillis = new SimpleDateFormat("HH:mm:ss.SSS")

  def toString(e: Throwable): String = {
    var sw: StringWriter = null
    try {
      sw = new StringWriter()
      val pw = new PrintWriter(sw)
      e.printStackTrace(pw)
      sw.toString
    } finally {
      if (sw ne null) sw.close()
    }
  }

  @inline
  def beforeDateTimeString() = formatDateTime print DateTime.now.minusDays(1)

  @inline
  def afterDateTimeString() = formatDateTime print DateTime.now.plusDays(1)

  @inline
  def curDateTimeString() = formatDateTime print DateTime.now

  @inline
  def curDateString() = formatDate print LocalDate.now

  @inline
  def curTimeString() = formatTime print LocalTime.now

  @inline
  def nextDateString() = formaterDate format new java.util.Date(System.currentTimeMillis() + perDayMillis)

  @inline
  def currentTimeMillis() = System.currentTimeMillis()

  @inline
  def emailValidate(email: String): Boolean = {
    emailer.matcher(email).matches
  }

  def NotImplement = throw new RuntimeException("未实现")

  @inline
  def ySha1(data: String): String =
    ySha1(data.getBytes)

  @inline
  def ySha1(data: Array[Byte]): String =
    new ByteArray2String(sha1.digest(data)).__HexString

  @inline
  def ySha256(data: String): String =
    ySha256(data.getBytes)

  @inline
  def ySha256(data: Array[Byte]): String =
    new ByteArray2String(sha256.digest(data)).__HexString

  @inline
  def ySha512(data: String): String =
    ySha512(data.getBytes)

  @inline
  def ySha512(data: Array[Byte]): String =
    new ByteArray2String(sha512.digest(data)).__HexString

  @inline
  def dumpDate[V <: Date](date: V) {
    println(formaterDate.format(date))
  }

  @inline
  def dumpDate(date: Calendar) {
    println(formaterDate.format(date.getTime))
  }

}
