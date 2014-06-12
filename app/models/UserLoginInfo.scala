package models

case class UserLoginInfo(
  id: Option[Long],
  providerId: String,
  providerKey: String,
  userId: Long
)
