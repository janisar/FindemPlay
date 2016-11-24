package model

import org.apache.commons.codec.binary.Base64
import play.api.libs.json._


case class FindemFile(id: Int, base64: String)

object FindemFile {
  implicit object FindemFileFormat extends Format[FindemFile] {

    override def reads(json: JsValue): JsResult[FindemFile] = {
      (json \ "id").validate[Int].flatMap(id => {
        (json \ "file").validate[String].map(file => {

          FindemFile(id, file)
        })
      })
    }

    override def writes(o: FindemFile): JsValue = JsObject(
      Seq(
        "id" -> JsNumber(o.id),
        "file" -> JsString(o.base64)
      )
    )
  }
}

case class FindemObject(genericName: String,
                        mapDrawings: Array[MapDrawing],
                        description: String,
                        files: Array[FindemFile])

object FindemObject {
  implicit object AddableFormat extends Format[FindemObject] {

    override def writes(o: FindemObject): JsValue = JsObject(
      Seq(
        "genericName" -> JsString(o.genericName),
        "description" -> JsString(o.description),
        "mapDrawings" -> Json.toJson(o.mapDrawings)
      )
    )

    override def reads(json: JsValue): JsResult[FindemObject] = {
      (json \ "genericName").validate[String].flatMap(genericName => {
        (json \ "mapDrawings").validate[Array[MapDrawing]].flatMap(mapDrawings => {
          (json \ "description").validate[String].flatMap(description => {
            (json \ "files").validate[Array[FindemFile]].map(

              files => FindemObject(genericName, mapDrawings, description, files)
            )
          })
        })
      })
    }

  }
}

