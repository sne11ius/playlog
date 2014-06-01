package models.database

import play.api.db.slick.Config.driver.simple._
import java.sql.Date
import scala.slick.lifted.Tag
import models.Post

class Posts(tag: Tag) extends Table[Post](tag, "post") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column
  def title = column[String]("title", O.DBType("text"))
  def body = column[String]("body", O.DBType("text"))
  def created = column[Date]("date")
  def edited = column[Date]("edited")
  def published = column[Boolean]("published")
  def author = column[String]("author")
  def * = (id.?, title, body, created, edited, published, author) <> (Post.tupled, Post.unapply _)
}

object Posts {
  val posts = TableQuery[Posts]
  
  def findAll()(implicit s: Session) = {
    posts.list
  }
  
  def insert(post: Post)(implicit s: Session) {
    posts.insert(post)
  }
  
  def delete(postId: Long)(implicit s: Session) {
    (posts filter(_.id === postId)).delete
  }
}