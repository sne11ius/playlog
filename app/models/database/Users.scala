/*
  case class User(
  userID: UUID,
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarURL: Option[String]
) extends Identity
*/
/*
case class LoginInfo(providerID: String, providerKey: String)
*/
package models.database

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import models.User
import com.mohiva.play.silhouette.core.LoginInfo
import scala.concurrent.Future
import play.api.db.slick._
import play.api.Play.current
import models.database.UserLoginInfos

class Users(tag: Tag) extends Table[User](tag, "user") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def firstName = column[Option[String]]("firstName")
  def lastName = column[Option[String]]("lastName")
  def fullName = column[Option[String]]("fullName")
  def email = column[Option[String]]("email")
  def avatarUrl = column[Option[String]]("avatarUrl")

  /*
  def wrap(id: Long, loginInfo_providerID: String, loginInfo_providerKey: String, firstName: Option[String], lastName: Option[String], fullName: Option[String], email: Option[String], avatarUrl: Option[String]): Option[User] = {
    Some(User(Some(id), LoginInfo(loginInfo_providerID, loginInfo_providerKey), firstName, lastName, fullName, email, avatarUrl))
  }
  
  def unwrap(user: User) = {
    (user.id, user.loginInfo.providerID, user.loginInfo.providerKey, user.firstName, user.lastName, user.fullName, user.email, user.avatarUrl)
  }*/
  
  //def * = (id.?, loginInfo_providerID, loginInfo_providerKey, firstName, lastName, fullName, email, avatarUrl) <> (unwrap, wrap)  
  def * = (id.?, firstName, lastName, fullName, email, avatarUrl) <> (User.tupled, User.unapply _)  
}

object Users {
  val users = TableQuery[Users]
  
  def find(loginInfo: LoginInfo): Future[Option[User]] = {
    DB.withSession { implicit session =>
      val userId = UserLoginInfos.find(loginInfo)
      Future.successful(
        users.filter(user => user.id == userId).firstOption
      )
    }
  }
  
  def save(user: User): User = {
    DB.withSession { implicit session =>
      users.insert(user)
    }
    user
  }
}
