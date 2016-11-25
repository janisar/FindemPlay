package dao

import java.io.{File, FileInputStream, FileOutputStream}
import javax.inject.Inject

import model.FindemObject
import org.joda.time.DateTime
import org.springframework.util.FileCopyUtils
import play.api.Configuration

/**
  * Created by saarlane on 24/11/16.
  */
class FileDao @Inject()(configuration: Configuration, missingObjectDao: MissingObjectDao) {

  def saveFile(objectId: Long, file: File): Unit = {

    def getFileName: String = {
      "findemImages/image-" + objectId + "-" + new DateTime()
    }
    val fileName = getFileName

    FileCopyUtils.copy(new FileInputStream(file), new FileOutputStream("public/" + fileName))
    val missingObject = missingObjectDao.getById(objectId)

    val filePaths = missingObject.filePaths ::: List("assets/" + fileName)
    val findemObject = FindemObject(missingObject.genericName, missingObject.mapDrawings, missingObject.description, filePaths)

    missingObjectDao.updateFindemObject(findemObject, objectId.toInt)
  }
}
