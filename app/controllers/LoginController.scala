package controllers

import javax.inject.Inject

import dao.UserDao
import model.LoginUser
import play.api.mvc.{Action, Results}

/**
  * Created by saarlane on 24/11/16.
  */
class LoginController @Inject() (userDao: UserDao) {

  def login = Action { request =>
    val loginUser: LoginUser = request.body.asJson.get.as[LoginUser]

    val validationResult = userDao.validateLogin(loginUser)
    if (!validationResult.isEmpty) {
      Results.Ok(validationResult)
    } else {
      Results.Unauthorized
    }
  }

  def register = Action { request =>
    val user: LoginUser = request.body.asJson.get.as[LoginUser]

    if (userDao.register(user)) {
      Results.Ok
    } else {
      Results.Unauthorized
    }
  }
}
