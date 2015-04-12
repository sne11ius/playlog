package service

import models.GeoCoord

trait GeoTrackerService {

  def getLatest: Option[GeoCoord]

}
