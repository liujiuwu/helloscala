package sample.common

import java.util.Random

object Tools {
  final val letterChar = ('a' to 'z').mkString + ('A' to 'Z').mkString
  final val numberChar = (0 to 9).mkString
  final val allChar = numberChar + letterChar

  def randomString(length: Int) = {
    val random = new Random()
    val size = allChar.length
    (0 until length).map(_ => allChar.charAt(random.nextInt(size))).mkString
  }

  val _md5 = DigestHelpers("MD5")
  val _sha1 = DigestHelpers("SHA1")
  val _sha256 = DigestHelpers("SHA256")
  val _sha512 = DigestHelpers("SHA512")

  @inline
  def md5(data: Array[Byte]): Array[Byte] =
    _md5.digest(data)

  @inline
  def md5(s: String): String =
    new String(md5(s.getBytes))

  @inline
  def sha1(data: Array[Byte]): Array[Byte] =
    _sha1.digest(data)

  @inline
  def sha1(s: String): String =
    new String(sha1(s.getBytes))

  @inline
  def sha256(data: Array[Byte]): Array[Byte] =
    _sha256.digest(data)

  @inline
  def sha256(s: String): String =
    new String(sha256(s.getBytes))

  @inline
  def sha512(data: Array[Byte]): Array[Byte] =
    _sha512.digest(data)

  @inline
  def sha512(s: String): String =
    new String(sha512(s.getBytes))
}
