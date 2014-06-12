package models

import com.mohiva.play.silhouette.core.{LoginInfo, Identity}
import com.mohiva.play.silhouette.core.LoginInfo

case class User(
  id: Option[Long],
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarUrl: Option[String],
  loginInfo: LoginInfo
) extends Identity
