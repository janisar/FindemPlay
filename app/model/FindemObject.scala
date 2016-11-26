package model

import org.joda.time.DateTime
import play.api.libs.json._

case class FindemObject(objectType: Int,
                        genericName: String,
                        firstName: String,
                        lastName: String,
                        mapDrawings: Array[MapDrawing],
                        description: String,
                        created: DateTime,
                        filePaths: List[String])

object FindemObject {
  implicit object AddableFormat extends Format[FindemObject] {

    override def writes(o: FindemObject): JsValue = {
      val customFields = if (o.objectType == 0) {
        Seq("genericName" -> JsString(o.genericName))
      } else {
        Seq("firstName" -> JsString(o.firstName),
            "lastName" -> JsString(o.lastName))
      }
      JsObject(
        customFields ++
          Seq(
          "objectType" -> JsNumber(o.objectType),
          "description" -> JsString(o.description),
          "mapDrawings" -> Json.toJson(o.mapDrawings),
          "created" -> JsString(o.created.toString),
          "filePaths" -> Json.toJson(o.filePaths)
        )
      )
    }

    override def reads(json: JsValue): JsResult[FindemObject] = {
      def createForObject(objectType: Int, genericName: String, mapDrawings: Array[MapDrawing], filePaths: List[String]): JsResult[FindemObject] = {
        (json \ "description").validateOpt[String].map {
          case Some(desc) => FindemObject(objectType, genericName, "", "",
            mapDrawings, desc,
            new DateTime, filePaths)

          case None => FindemObject(objectType, genericName, "", "",
            mapDrawings, "",
            new DateTime, filePaths)
        }
      }

      def createForPerson(objectType: Int, firstName: String, lastName: String, mapDrawings: Array[MapDrawing], filePaths: List[String]): JsResult[FindemObject] = {
        (json \ "description").validateOpt[String].map {
          case Some(desc) => FindemObject(objectType, "", firstName, lastName,
            mapDrawings, desc,
            new DateTime, filePaths)

          case None => FindemObject(objectType, "", firstName, lastName,
            mapDrawings, "",
            new DateTime, filePaths)
        }
      }

      (json \ "objectType").validate[Int].flatMap {
        case 0 => {
          (json \ "genericName").validate[String].flatMap(genericName => {
            (json \ "mapDrawings").validate[Array[MapDrawing]].flatMap(mapDrawings => {
              (json \ "filePaths").validateOpt[List[String]].flatMap {
                case Some(filePaths) =>
                  createForObject(0, genericName, mapDrawings, filePaths)
                case None =>
                  createForObject(0, genericName, mapDrawings, List())
              }
            })
          })
        }
        case 1 => {
          (json \ "firstName").validate[String].flatMap(firstName => {
            (json \ "lastName").validate[String].flatMap(lastName => {
              (json \ "mapDrawings").validate[Array[MapDrawing]].flatMap(mapDrawings => {
                (json \ "filePaths").validateOpt[List[String]].flatMap {
                  case Some(filePaths) =>
                    createForPerson(1, firstName, lastName, mapDrawings, filePaths)
                  case None =>
                    createForPerson(1, firstName, lastName, mapDrawings, List())
                }
              })
            })
          })
        }
      }
    }
  }
}

