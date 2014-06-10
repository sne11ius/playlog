package models.database

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import com.github.tototoshi.slick.MySQLJodaSupport._
import models.Post
import org.joda.time.DateTime

class Posts(tag: Tag) extends Table[Post](tag, "post") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title", O.DBType("text"))
  def body = column[String]("body", O.DBType("text"))
  def created = column[DateTime]("date")
  def edited = column[DateTime]("edited")
  def published = column[Boolean]("published")
  def author = column[String]("author")
  def * = (id.?, title, body, created, edited, published, author) <> (Post.tupled, Post.unapply _)
}

object Posts {
  val posts = TableQuery[Posts]
  
  def findAll()(implicit s: Session) = {
    posts.sortBy(p => p.created.desc).list
  }
  
  def findAllPublished()(implicit s: Session) = {
	  posts.sortBy(p => p.created.desc).filter(_.published === true).list
  }
  
  def find(postId: Long)(implicit s: Session): Post = {
    (posts filter(_.id === postId)).first
  }
  
  def insert(post: Post)(implicit s: Session) {
    posts.insert(post)
  }
  
  def update(post: Post)(implicit s: Session) {
    posts.filter(_.id === post.id) update(post)
  }
  
  def delete(postId: Long)(implicit s: Session) {
    (posts filter(_.id === postId)).delete
  }
  
  def deleteAll()(implicit s: Session) {
    findAll.toList.map(post => {
	  if (!post.id.isEmpty) {
	    Posts.delete(post.id.get)
	  }
	})
  }
}
