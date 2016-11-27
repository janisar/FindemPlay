package model

import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json._

/**
  * Created by saarlane on 19/11/16.
  */
case class LoginUser(
                     userName: String,
                     password: String,
                     email: String,
                     salt: String)

object LoginUser {
  implicit object LoginUserFormat extends Format[LoginUser] {
    override def reads(json: JsValue): JsResult[LoginUser] = {
      (json \ "userName").validate[String].flatMap(userName => {
        (json \ "email").validate[String].flatMap(email => {
          (json \ "password").validate[String].map(password => {

            saveUser(userName, email, password)
          })
        })
      })
    }

    override def writes(o: LoginUser): JsValue = JsObject(
      Seq(
        "email" -> JsString(o.email),
        "userName" -> JsString(o.userName)
      )
    )
  }

  def saveUser(userName: String, email: String, password: String): LoginUser = {
    val salt = BCrypt.gensalt(10)
    LoginUser(userName, password, email, salt)
  }
}
