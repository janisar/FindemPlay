package controllers

import javax.inject.{Inject, Singleton}

import dao.AddableDao
import model.LoginUser
import play.api.mvc._



@Singleton
class Application @Inject() (addableDao: AddableDao) extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def saveObject = Action { request =>

    val json = request.body.asJson.get
    addableDao.saveAddable(json.toString())

    Ok("All good")
  }

  def login = Action { request =>
    val loginUser: LoginUser = request.body.asJson.get.as[LoginUser]

    val successful = addableDao.validateLogin(loginUser)
    if (successful) {
      Ok
    } else {
      Unauthorized
    }
  }

  def register = Action { request =>
    val user: LoginUser = request.body.asJson.get.as[LoginUser]

    if (addableDao.register(user)) {
      Ok
    } else {
      Unauthorized
    }
  }
}
