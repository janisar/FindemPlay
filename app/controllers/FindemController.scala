package controllers

import javax.inject.Inject

import dao.MissingObjectDao
import play.api.libs.json.Json
import play.api.mvc.{Action, Results}

/**
  * Created by saarlane on 24/11/16.
  */
class FindemController @Inject() (missingObjectDao: MissingObjectDao) {

  def saveObject = Action { request =>
    val json = request.body.asJson.get
    val id = missingObjectDao.saveAddable(json.toString())

    Results.Ok(id.toString)
  }

  def getObjects = Action { request =>
    val result = Json.stringify(Json.toJson(missingObjectDao.getMissingObjects()))

    Results.Ok(result)
  }
}
