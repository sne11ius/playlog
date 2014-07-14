package models

import org.joda.time.DateTime

case class Comment(
  id: Option[Long],
  title: String,
  body: String,
  created: DateTime,
  edited: DateTime,
  author: User
)