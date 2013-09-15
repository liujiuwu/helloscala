package sample.squeryl.basic.model

import org.squeryl.KeyedEntity
import org.joda.time.DateTime

import sample.common.{Tools, SexGenre}
import sample.squeryl.basic.helper.SquerylEntrypoint._
import scala.util.Try

/**
 * 演示了Squeryl的查询、修改、添加、删除功能。并演示了怎样自行实现基于salt的密码验证。
 */
object User {
  /**
   * 获得表长度
   *
   * @return
   */
  def size(
            _id: Option[String] = None,
            sex: Option[SexGenre.Value] = None,
            startTime: Option[DateTime] = None,
            endTime: Option[DateTime] = None): Long =
    transaction {
      val id = _id.map("%" + _ + "%")
      from(Entities.users)(u =>
        where(
          (u.id like id.?) and
            (u.sex === sex.?) and
            (u.created_at gte startTime.?) and
            (u.created_at lt endTime.?)
        ) compute (count(u.id)))
    }

  /**
   * 判断用户是否存在
   *
   * @param id
   * @return 存在返回true，反之返回false
   */
  def exist(id: String): Boolean =
    transaction(from(Entities.users)(u =>
      where(u.id === id) compute (count(u.id)))) > 0L

  /**
   * 动态查询示例
   *
   * @param sex
   * @param startTime
   * @param endTime
   * @return  符合搜索条件的结果集
   */
  def finds(
             sex: Option[SexGenre.Value] = None,
             startTime: Option[DateTime] = None,
             endTime: Option[DateTime] = None): List[User] =
    transaction(from(Entities.users)(u =>
      where(
        (u.sex === sex.?) and
          (u.created_at gte startTime.?) and
          (u.created_at lt endTime.?)
      ) select (u)).toList)

  /**
   * 查找用户
   *
   * @param id
   * @return
   */
  def find(id: String): Option[User] =
    transaction(
      from(Entities.users)(u =>
        where(u.id === id) select (u)).singleOption)

  def find(id: String, password: String): Option[User] =
    transaction(
      from(Entities.userPasswords)(u =>
        where(u.id === id) select (u)).singleOption) filter (up =>
      up.password == Tools.sha1(password + up.salt)) flatMap (up =>
      find(up.id))

  def add(user: User, password: String): Try[User] =
    Try(transaction {
      val salt = Tools.randomString(8)
      val userPassword = new UserPassword(user.id, Tools.sha1(password + salt), salt)

      val result = Entities.users insert user
      Entities.userPasswords insert userPassword
      result
    })

  def modify(user: User) =
    Try(transaction(Entities.users update user))

  def modify(userId: String, newPassword: String): Try[Int] =
    Try(transaction {
      val salt = Tools.randomString(8)
      val password = Tools.sha1(newPassword + salt)
      update(Entities.userPasswords)(up =>
        where(up.id === userId)
          set(up.salt := salt, up.password := password))
    })

  def modify(userId: String, oldPassword: String, newPassword: String): Try[Int] =
    find(userId, oldPassword) match {
      case Some(_) =>
        modify(userId, newPassword)

      case None =>
        util.Failure(new IllegalArgumentException("用户不存在或当前密码错误"))
    }

  def remove(userId: String): Try[Int] =
    Try(transaction {
      Entities.userPasswords.deleteWhere(_.id === userId)
      Entities.users.deleteWhere(_.id === userId)
    })

  def remove(userId: String, password: String): Try[Int] =
    find(userId, password) match {
      case Some(_) =>
        remove(userId)

      case None =>
        util.Failure(new IllegalArgumentException("用户不存在或当前密码错误"))
    }
}

class User(
            var id: String,
            var age: Int,
            var sex: SexGenre.Value,
            var phone: String,
            val created_at: DateTime = DateTime.now) extends KeyedEntity[String] {
  def this() = this("", 0, SexGenre.Neutral, "")
}

class UserPassword(
                    val id: String,
                    val password: String,
                    val salt: String)
