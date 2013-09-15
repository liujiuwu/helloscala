package yangbajing.util

import java.util.concurrent.atomic.AtomicLong

import net.liftweb.util.StringHelpers

trait BaseRandom {
  val atomicLong = new AtomicLong

  def nextLong: Long = atomicLong.incrementAndGet()

  def nextKey: String = nextKey(0L)

  def nextKey(seed: Long): String = {
    val sb = new StringBuilder(20)
    sb append 'y'
    sb append "%016x".format(nextLong)
    sb append StringHelpers.randomString(3)
    sb.toString
  }
}
