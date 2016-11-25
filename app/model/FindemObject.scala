package model

import play.api.libs.json._

case class FindemObject(genericName: String,
                        mapDrawings: Array[MapDrawing],
                        description: String,
                        filePaths: List[String])

object FindemObject {
  implicit object AddableFormat extends Format[FindemObject] {

    override def writes(o: FindemObject): JsValue = JsObject(
      Seq(
        "genericName" -> JsString(o.genericName),
        "description" -> JsString(o.description),
        "mapDrawings" -> Json.toJson(o.mapDrawings),
        "filePaths" -> Json.toJson(o.filePaths)
      )
    )

    override def reads(json: JsValue): JsResult[FindemObject] = {
      (json \ "genericName").validate[String].flatMap(genericName => {
        (json \ "mapDrawings").validate[Array[MapDrawing]].flatMap(mapDrawings => {
          (json \ "filePaths").validateOpt[List[String]].flatMap {
            case Some(filePaths) => {
              (json \ "description").validateOpt[String].map {
                case Some(desc) => FindemObject(genericName, mapDrawings, desc, filePaths)
                case None => FindemObject(genericName, mapDrawings, "", filePaths)
              }
            }
            case None => {
              (json \ "description").validateOpt[String].map {
                case Some(desc) => FindemObject(genericName, mapDrawings, desc, List())
                case None => FindemObject(genericName, mapDrawings, "", List())
              }
            }
          }
        })
      })
    }
  }
}

