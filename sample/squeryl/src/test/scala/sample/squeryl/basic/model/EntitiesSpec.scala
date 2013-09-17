package sample.squeryl.basic.model

import org.scalatest.{Matchers, BeforeAndAfterAll, FunSpec}

import sample.squeryl.basic.helper.RdbHelpers
import sample.common.SexGenre

class EntitiesSpec extends FunSpec with Matchers with BeforeAndAfterAll {

  describe("User 测试") {
    it("建立用户") {
      val user = new User("yangbajing", 27, SexGenre.Neutral, "023-12345678")
      User.add(user, "yangbajing").isSuccess should be(true)
    }

    it("修改用户") {
      User.find("yangbajing") match {
        case Some(user) =>
          user.age = 28
          user.phone = "023-87654321"
          user.sex = SexGenre.Male
          User.modify(user).isSuccess should be(true)

        case None =>
          assert(false)
      }
    }

    it("查询用户") {
      val result = User.find("yangbajing", "yangbajing")
      result.isEmpty should not be true

      val user = result.get
      assert(user.age === 28)
      assert(user.sex === SexGenre.Male)
      assert(user.phone === "023-87654321")
    }

    it("删除用户") {
      User.remove("yangbajing", "yangbajing").isFailure should not be true
    }
  }

  import sample.squeryl.basic.helper.SquerylEntrypoint._

  override def beforeAll() {
    RdbHelpers.registerSquerylSession()
    println("测试开始，建立测试表！")
    transaction {
      Entities.printDdl
      Entities.create
    }
  }

  override def afterAll() {
    println("测试完成，删除测试表！")
    transaction(Entities.drop)
  }

}
