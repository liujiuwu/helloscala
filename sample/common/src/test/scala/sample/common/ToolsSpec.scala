package sample.common

import org.scalatest.{Matchers, FunSpec}

class ToolsSpec extends FunSpec with Matchers {

  describe("随机字符串测试") {
    it("字符长度测试") {
      assert(Tools.allChar == "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
    }

    it("6位随机字符串测试") {
      val ss = (0 until 100).map(_ => Tools.randomString(6)).filter(_.length == 6).toSet

      ss.size shouldBe 100
    }
  }

}
