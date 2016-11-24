package controllers

import javax.inject.Inject

import akka.stream.Materializer
import dao.FileDao
import org.apache.tika.Tika
import play.api.mvc.{Action, Results}
import play.api.mvc.BodyParsers.parse

/**
  * Created by saarlane on 24/11/16.
  */
class FileController @Inject() (fileDao: FileDao,
                                implicit val mat: Materializer) {

  val ACCEPTED_FILE_TYPES = List("image/png", "image/jpg", "image/jpeg")

  def saveFile(id: Long) = Action(parse.maxLength(10 * 1024 * 1024, parse.multipartFormData)) { request =>
    request.body.fold(fa => {
      print("Maximum file size exceeded")
    }, fb => {
      fb.files.foreach(file => {
        val tika: Tika = new Tika()
        val fileType = tika.detect(file.ref.file)

        if (ACCEPTED_FILE_TYPES.contains(fileType)) {
          fileDao.saveFile(id, file.ref.file)
        } else {

        }
      })
    })

    Results.Ok("ok")
  }
}
