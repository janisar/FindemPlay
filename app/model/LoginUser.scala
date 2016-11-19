package model

import play.api.libs.json._

/**
  * Created by saarlane on 19/11/16.
  */
case class LoginUser(userName: String, password: String, email: String)

object LoginUser {
  implicit object LoginUserFormat extends Format[LoginUser] {
    override def reads(json: JsValue): JsResult[LoginUser] = {
      (json \ "userName").validate[String].flatMap(userName => {
        (json \ "email").validate[String].flatMap(email => {
          (json \ "password").validate[String].map(password => {
            LoginUser(userName, password, email)
          })
        })
      })
    }

    override def writes(o: LoginUser): JsValue = JsObject(
      Seq(
        "email" -> JsString(o.email),
        "userName" -> JsString(o.userName),
        "password" -> JsString(o.password)
      )
    )
  }
}
