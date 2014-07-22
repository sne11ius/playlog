package service

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import models.Post
import models.Comment
import org.joda.time.DateTime
import java.util.Date
import javax.inject.Inject
import models.daos.UserDAO
import play.api.Play.current
import models.daos.DBTableDefinitions._
import play.Logger
import org.joda.time.DateTimeZone
import java.util.UUID

class PostServiceImpl @Inject() (userDAO: UserDAO) extends PostService {

  override def findAll: List[Post] = {
    DB withSession { implicit session =>
      val allUsers = userDAO.findAll
      slickPosts.sortBy(p => p.created.desc).list.map(p => {
        val comments = slickComments.sortBy(c => c.created.desc).filter(_.postId === p.id).list.map(c =>
          Comment(c.id, c.title, c.body, new DateTime(c.created), new DateTime(c.edited), allUsers(c.author))
        )
        Post(p.id, p.title, p.body, new DateTime(p.created), new DateTime(p.edited), p.published, allUsers(p.author), comments)
      })
    }
  }

  override def findAllPublished(inTitle: Option[String]): List[Post] = {
    DB withSession { implicit session =>
      val allUsers = userDAO.findAll
      inTitle match {
        case None =>
	      (slickPosts.sortBy(p => p.created.desc).filter(_.published === true) list).map(p => {
	        val comments = slickComments.sortBy(c => c.created.desc).filter(_.postId === p.id).list.map(c =>
	          Comment(c.id, c.title, c.body, new DateTime(c.created), new DateTime(c.edited), allUsers(c.author))
	        )
	        Post(p.id, p.title, p.body, new DateTime(p.created), new DateTime(p.edited), p.published, allUsers(p.author), comments)
	      })
        case Some(query) => {
          val strings = query
              .toLowerCase()
              .replace("-", "_")
              .replace(" ", "_")
              .replace("%20", "_")
              .split("_").toList
          //Logger.debug("Query: " + query)
          (slickPosts.sortBy(p => p.created.desc).filter(_.published === true) list).filter(p => {
        	  strings.forall(s => p.title.toLowerCase contains s)
          }).map(p => {
	        val comments = slickComments.sortBy(c => c.created.desc).filter(_.postId === p.id).list.map(c =>
	          Comment(c.id, c.title, c.body, new DateTime(c.created), new DateTime(c.edited), allUsers(c.author))
	        )
	        Post(p.id, p.title, p.body, new DateTime(p.created), new DateTime(p.edited), p.published, allUsers(p.author), comments)
	      })
        }
      }
    }
  }
  
  override def findSinglePost(date: DateTime, title: String): List[Post] = {
    val allPublished = findAllPublished(Some(title))
    //Logger.debug("Num all: " + allPublished.length)
    val minDate = date.plusDays(1)//.minusMillis(1)
    val maxDate = date.plusDays(2)
    //Logger.debug("minDate: " + minDate)
    //Logger.debug("maxDate: " + maxDate)
    val candidates = allPublished.filter(p => {
      val created = p.created.withZone(DateTimeZone.UTC)
      //Logger.debug("Created: " + created)
      created.isAfter(date) && created.isBefore(maxDate)
    })
    //Logger.debug("Num candidates: " + candidates.length)
    candidates.take(1)
  }
  
  override def find(postId: UUID): Post = {
    DB withSession { implicit session =>
      val p = (slickPosts filter(_.id === postId)).first
      val allUsers = userDAO.findAll
      val comments = slickComments.sortBy(c => c.created.desc).filter(_.postId === postId).list.map(c =>
        Comment(c.id, c.title, c.body, new DateTime(c.created), new DateTime(c.edited), allUsers(c.author))
      )
      Post(p.id, p.title, p.body, new DateTime(p.created), new DateTime(p.edited), p.published, allUsers(p.author), comments)
    }
  }
  
  override def insert(post: Post) = {
    DB withSession { implicit session =>
      slickPosts.insert(DBPost(post.id, post.title, post.body, post.created.getMillis(), post.edited.getMillis(), post.published, post.author.userID.toString()))
    }
  }
  
  override def update(post: Post) = {
    DB withSession { implicit session =>
      slickPosts.filter(_.id === post.id) update(DBPost(post.id, post.title, post.body, post.created.getMillis(), post.edited.getMillis(), post.published, post.author.userID.toString()))
    }
  }
  
  override def delete(postId: UUID) = {
    DB withSession { implicit session =>
      (slickPosts filter(_.id === postId)).delete
      (slickComments filter(_.postId === postId)).delete
    }
  }
  
  override def deleteAll = {
    DB withSession { implicit session =>
      slickPosts.delete
      slickComments.delete
    }
  }
  
  override def addComment(postId: UUID, comment: Comment) = {
    DB withSession { implicit session =>
      slickComments.insert(DBComment(comment.id, comment.title, comment.body, comment.created.getMillis(), comment.edited.getMillis(), comment.author.userID.toString(), postId))
    }
  }
}
