package com.helloscala.model

import org.scalatest.{BeforeAndAfterAll, Matchers, FunSpec}

import com.helloscala.helper.RdbHelpers

import yangbajing.persistence.SquerylEntrypoint._

class EntitiesTest extends FunSpec with Matchers with BeforeAndAfterAll {

  override protected def beforeAll() {
    RdbHelpers.registerSquerylSession()
  }

  describe("Entities ddl") {
    it("drop tables") {
      transaction(Entities.drop)
    }

    it("create tables") {
      transaction {
        Entities.printDdl
        Entities.create
      }
    }
  }

  describe("Entities user") {
    it("exists") {
      MUser.exists("ddd@ddd.com") shouldBe false
    }

    it("insert users") {
      val users = Seq(
        ("yangbajing@gmail.com", "羊八井", "yangbajing"),
        ("yang.xunjing@qq.com", "杨景", "yangjing")
      ).map {
        case (id, nick, password) =>
          val user = MUser(id, nick = Some(nick))
          MUser.createAndInsert(user, password)
      }

      users foreach println

      users.nonEmpty shouldBe true
    }

    it("select users") {
      var user = MUser.findOne("yang.xunjing@qq.com")
      println(user)
      user.isDefined shouldBe true

      user = MUser.findOne("yangbajing@gmail.com", Some("yangbajing"))
      println(user)
      user.isDefined shouldBe true
      user = MUser.findOne("yang.xunjing@qq.com", Some("jkjkjkjkjkjkj"))
      println(user)
      user.isDefined shouldBe false

      user = MUser.findOne("jkjk@jk.com", Some("yangjing"))
      println(user)
      user.isDefined shouldBe false
    }
  }

}
