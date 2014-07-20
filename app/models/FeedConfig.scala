package models

case class FeedConfig(
  title: String,
  subtitle: String,
  authorName: String,
  feedId: String,
  baseUrl: String,
  summaryLength: Int,
  copyright: String
)
