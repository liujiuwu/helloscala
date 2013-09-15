package com.helloscala.common

import org.scalatest.{Matchers, FunSpec}

class EnumTraitTest extends FunSpec with Matchers {

  describe("EnumTrait Test") {
    it("value") {
      val sex = SexCode.Male
      sex shouldBe SexCode.Male

      println(sex)
    }

    it("opts") {
      val opts = SexCode.opts
      opts.nonEmpty shouldBe true

      println(opts)
    }

    it("optIds") {
      val optIds = SexCode.optIds
      optIds.nonEmpty shouldBe true

      println(optIds)
    }

    it("optObjs") {
      val optObjs: List[(SexCode.Value, String)] = SexCode.optObjs
      optObjs.nonEmpty shouldBe true

      println(optObjs)
    }
  }
}
