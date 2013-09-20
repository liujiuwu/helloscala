package com.helloscala.helper

import org.scalatest.{Matchers, FunSpec}
import org.joda.time.DateTime

class HelloHelpersTest extends FunSpec with Matchers {
  describe("HelloHelpers time test") {
    val now = DateTime.now

    it("yearsBetween") {
      HelloHelpers.yearsBetween(now.minusYears(2), now) shouldBe Some(2)
      HelloHelpers.yearsBetween(now.minusMillis(1), now) shouldBe None
    }

    it("monthsBetween") {
      HelloHelpers.monthsBetween(now.minusMonths(7), now) shouldBe Some(7)
      HelloHelpers.monthsBetween(now, now.minusMonths(3)) shouldBe None
    }

    it("weeksBetween") {
      HelloHelpers.weeksBetween(now.minusWeeks(1), now) shouldBe Some(1)
      HelloHelpers.weeksBetween(now, now) shouldBe None
    }

    it("daysBetween") {
      HelloHelpers.daysBetween(now.minusDays(3123), now) shouldBe Some(3123)
      HelloHelpers.daysBetween(now.plusDays(1), now) shouldBe None
    }

  }

}
