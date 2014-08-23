package models.daos;

import models.YoSubscriber
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import models.daos.DBTableDefinitions._
import play.api.Play.current
import models.YoSubscriber

class YoSubscriberDAOSlick extends YoSubscriberDAO {
  def add(subscriber: YoSubscriber) = {
    DB withSession { implicit session =>
      val dbYoSubscriber = DBYoSubscriber(subscriber.id, subscriber.username)
      slickYoSubscribers insert dbYoSubscriber
    }
  }
  
  def findAll() : List[YoSubscriber] = {
    DB withSession { implicit session =>
      slickYoSubscribers.list.map(s => YoSubscriber(s.id, s.username) )
    }
  }
}
