package models

import org.joda.time.DateTime

case class GeoCoord (
  latitude: Double,
  longitude: Double,
  altitude: Double,
  accuracy: Float,
  speed: Float,
  time: DateTime
)
