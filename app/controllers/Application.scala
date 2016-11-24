package controllers

import javax.inject.{Inject, Singleton}

import akka.stream.Materializer
import dao.{FileDao, MissingObjectDao}
import play.api.mvc._

@Singleton
class Application @Inject() (missingObjectDao: MissingObjectDao,
                             fileDao: FileDao,
                             implicit val mat: Materializer) extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
}
