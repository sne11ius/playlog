package models.database

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import models.AdminIdentifier
import java.util.UUID
import play.api.Logger
import models.AdminIdentifier

class AdminIdentifiers(tag: Tag) extends Table[AdminIdentifier](tag, "admindidentifier") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def socialId = column[String]("socialId")
  def * = (id.?, socialId) <> (AdminIdentifier.tupled, AdminIdentifier.unapply _)
}

object AdminIdentifiers {
  val identifiers = TableQuery[AdminIdentifiers]
  
  def findAll()(implicit s: Session) = {
    identifiers.list
  }
  
  def findByUserId(socialId: UUID)(implicit s: Session): Option[AdminIdentifier] = {
    identifiers.filter(_.socialId === socialId.toString()).firstOption
  } 
  
  def insert(adminIdentifier: AdminIdentifier)(implicit s: Session) {
    identifiers.insert(adminIdentifier)
  }
}
