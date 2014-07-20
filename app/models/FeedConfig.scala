package models

case class FeedConfig(
  title: String,
  subtitle: String,
  authorName: String,
  feedId: String,
  baseUrl: String,
  summaryLength: Int,
  bodyLength: Int,
  copyright: String
)

object FeedConfig extends FeedConfig(
  title = "wasis.nu/mit/blog",
  subtitle = "Blog about technical stuff.",
  authorName = "Cornelius Lilge",
  feedId = "urn:uuid:14369a20-1023-11e4-9191-0800200c9a66",
  baseUrl = "http://wasis.nu/mit/blog",
  summaryLength = 200,
  bodyLength = 1000,
  copyright = "(c) 2014 - wasis.nu"
)