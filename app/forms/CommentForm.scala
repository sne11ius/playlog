package forms

import play.api.data.Form
import play.api.data.Forms._

object CommentForm {

  val form = Form(
    mapping(
      "title" -> nonEmptyText,
      "body" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(
    title: String,
    body: String
  )
}
