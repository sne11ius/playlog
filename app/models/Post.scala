package models

import org.joda.time.DateTime

case class Post(
  id: Option[Long],
  title: String,
  body: String,
  created: DateTime,
  edited: DateTime,
  published: Boolean,
  author: User,
  comments: List[Comment]
)
