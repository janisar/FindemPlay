package dao

import java.io.{File, FileInputStream, FileOutputStream}
import javax.inject.Inject

import org.joda.time.DateTime
import org.springframework.util.FileCopyUtils
import play.api.Configuration

/**
  * Created by saarlane on 24/11/16.
  */
class FileDao @Inject()(configuration: Configuration) {

  def saveFile(objectId: Long, file: File): Unit = {

    def getFileName: String = {
      "findemImages/image-" + objectId + "-" + new DateTime()
    }

    FileCopyUtils.copy(new FileInputStream(file), new FileOutputStream(new File(getFileName)))
  }
}
