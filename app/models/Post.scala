package models

import java.sql.Date

case class Post(
  id: Option[Long],
  title: String,
  body: String,
  created: Date,
  edited: Date,
  published: Boolean,
  author: String
)
