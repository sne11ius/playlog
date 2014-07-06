package models.database

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import models.Post
import org.joda.time.DateTime

case class DBPost(
  id: Option[Long],
  title: String,
  body: String,
  created: Long,
  edited: Long,
  published: Boolean,
  author: String
)

class Posts(tag: Tag) extends Table[DBPost](tag, "post") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title", O.DBType("text"))
  def body = column[String]("body", O.DBType("text"))
  def created = column[Long]("date")
  def edited = column[Long]("edited")
  def published = column[Boolean]("published")
  def author = column[String]("author")
  def * = (id.?, title, body, created, edited, published, author) <> (DBPost.tupled, DBPost.unapply)
}

object Posts {
  val posts = TableQuery[Posts]
  
  def findAll()(implicit s: Session) = {
    posts.sortBy(p => p.created.desc).list
  }
  
  def findAllPublished()(implicit s: Session): List[Post] = {
    (posts.sortBy(p => p.created.desc).filter(_.published === true) list).map(p => Post(p.id, p.title, p.body, new DateTime(p.created), new DateTime(p.edited), p.published, p.author))
  }
  
  def find(postId: Long)(implicit s: Session): Post = {
    val p = (posts filter(_.id === postId)).first
    Post(p.id, p.title, p.body, new DateTime(p.created), new DateTime(p.edited), p.published, p.author)
  }
  
  def insert(post: Post)(implicit s: Session) {
    posts.insert(DBPost(post.id, post.title, post.body, post.created.getMillis(), post.edited.getMillis(), post.published, post.author))
  }
  
  def update(post: Post)(implicit s: Session) {
    posts.filter(_.id === post.id) update(DBPost(post.id, post.title, post.body, post.created.getMillis(), post.edited.getMillis(), post.published, post.author))
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
