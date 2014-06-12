package models.database

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import com.mohiva.play.silhouette.core.LoginInfo
import models.UserLoginInfo
import models.UserLoginInfo
import models.UserLoginInfo
import play.api.db.slick._
import play.api.Play.current

class UserLoginInfos(tag: Tag) extends Table[UserLoginInfo](tag, "userlogininfo") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def providerId = column[String]("providerId")
  def providerKey = column[String]("providerKey")
  def userId = column[Long]("userId")
  
  def * = (id.?, providerId, providerKey, userId) <> (UserLoginInfo.tupled, UserLoginInfo.unapply _)
}

object UserLoginInfos {
  val infos = TableQuery[UserLoginInfos]
  
  def save(info: UserLoginInfo) = {
    DB.withSession { implicit session =>
      infos.insert(info)
    }
  }
  
  def find(loginInfo: LoginInfo)(implicit s: Session): Option[Long] = {
    Some(((infos.filter(i => {
      i.providerId == loginInfo.providerID &&
      i.providerKey == loginInfo.providerKey
    })) first).userId)
	/*
    ((infos.filter(i => {
      i.providerId == loginInfo.providerID && i.providerKey == loginInfo.providerKey
    }))
    */
  }
}