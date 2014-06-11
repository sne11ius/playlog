package models.database

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import com.github.tototoshi.slick.MySQLJodaSupport._
import models.AdminIdentifier
import java.util.UUID
import play.api.Logger

class AdminIdentifiers(tag: Tag) extends Table[AdminIdentifier](tag, "admindidentifier") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def socialId = column[UUID]("socialId")
  def * = (id.?, socialId) <> (AdminIdentifier.tupled, AdminIdentifier.unapply _)
}

object AdminIdentifiers {
  val identifiers = TableQuery[AdminIdentifiers]
  
  def findAll()(implicit s: Session) = {
    identifiers.list
  }
  
  def findBySocialId(socialId: UUID)(implicit s: Session) = {
    Logger.debug(findAll().toString)
    Logger.debug("==> " + socialId)
    identifiers.filter(identifier => identifier.socialId == socialId).firstOption
  } 
  
  def insert(adminIdentifier: AdminIdentifier)(implicit s: Session) {
    identifiers.insert(adminIdentifier)
  }
}
