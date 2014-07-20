package models

import org.joda.time.DateTime
import java.util.UUID

case class Post(
  id: UUID,
  title: String,
  body: String,
  created: DateTime,
  edited: DateTime,
  published: Boolean,
  author: User,
  comments: List[Comment]
)
