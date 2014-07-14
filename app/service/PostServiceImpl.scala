package service

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import models.Post
import org.joda.time.DateTime
import javax.inject.Inject
import models.daos.UserDAO
import play.api.Play.current
//import models.daos.DBTableDefinitions
import models.daos.DBTableDefinitions._

class PostServiceImpl @Inject() (userDAO: UserDAO) extends PostService {

  //val posts = DBTableDefinitions.slickPosts
  
  override def findAll: List[Post] = {
    DB withSession { implicit session =>
      val allUsers = userDAO.findAll
      slickPosts.sortBy(p => p.created.desc).list.map(p => {
        Post(p.id, p.title, p.body, new DateTime(p.created), new DateTime(p.edited), p.published, allUsers(p.author))
      })
    }
  }

  override def findAllPublished: List[Post] = {
    DB withSession { implicit session =>
      val allUsers = userDAO.findAll
      (slickPosts.sortBy(p => p.created.desc).filter(_.published === true) list).map(p => {
        Post(p.id, p.title, p.body, new DateTime(p.created), new DateTime(p.edited), p.published, allUsers(p.author))
      })
    }
  }
  
  override def find(postId: Long): Post = {
    DB withSession { implicit session =>
      val p = (slickPosts filter(_.id === postId)).first
      val allUsers = userDAO.findAll
      Post(p.id, p.title, p.body, new DateTime(p.created), new DateTime(p.edited), p.published, allUsers(p.author))
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
  
  override def delete(postId: Long) = {
    DB withSession { implicit session =>
      (slickPosts filter(_.id === postId)).delete
    }
  }
  
  override def deleteAll = {
    DB withSession { implicit session =>
      slickPosts delete
    }
  }
}
