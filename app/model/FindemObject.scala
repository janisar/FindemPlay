package model

import play.api.libs.json._

/**
  * Created by saarlane on 19/11/16.
  */
case class FindemObject(genericName: String,
                        mapDrawings: Array[MapDrawing],
                        description: String,
                        files: Array[String])

object FindemObject {
  implicit object AddableFormat extends Format[FindemObject] {

    override def writes(o: FindemObject): JsValue = JsObject(
      Seq("genericName" -> JsString(o.genericName))
    )

    override def reads(json: JsValue): JsResult[FindemObject] = {
      (json \ "genericName").validate[String].flatMap(genericName => {
        (json \ "mapDrawings").validate[Array[MapDrawing]].flatMap(mapDrawings => {
          (json \ "description").validate[String].flatMap(description => {
            (json \ "files").validate[Array[String]].map(
              files => FindemObject(genericName, mapDrawings, description, files)
            )
          })
        })
      })
    }

  }
}

