package models

import java.sql.Date
import org.joda.time.DateTime

case class Post(
  id: Option[Long],
  title: String,
  body: String,
  created: DateTime,
  edited: DateTime,
  published: Boolean,
  authorId: String
)
