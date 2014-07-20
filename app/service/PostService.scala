package service

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import models.Post
import models.Comment
import javax.inject.Inject
import models.daos.UserDAO
import org.joda.time.DateTime
import java.util.UUID

trait PostService {
  
  def findAll: List[Post]

  def findAllPublished(inTitle: Option[String]): List[Post]
  
  def findSinglePost(date: DateTime, title: String): List[Post]
  
  def find(postId: UUID): Post
  
  def insert(post: Post)
  
  def update(post: Post)
  
  def delete(postId: UUID)
  
  def deleteAll
  
  def addComment(postId: UUID, comment: Comment)
}
