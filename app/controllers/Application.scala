package controllers

import javax.inject.{Inject, Singleton}

import dao.AddableDao
import play.api.libs.json._
import play.api.mvc._

case class Point(lat: Double, lng: Double)

object Point {
  implicit object PointFormat extends Format[Point] {
    override def reads(json: JsValue): JsResult[Point] = {
      (json \ "lat").validate[Double].flatMap(lat => {
        (json \ "lng").validate[Double].map(lng => {
          Point(lat, lng)
        })
      })
    }

    override def writes(o: Point): JsValue = JsObject(
      Seq("lat" -> JsNumber(o.lat), "lng" -> JsNumber(o.lng))
    )
  }
}

case class Rect(rectId: Int, top: Double, right: Double, left: Double, bottom: Double) extends MapDrawing(rectId)

case class Circle(circleId: Int, radius: Double, center: Point) extends MapDrawing(circleId)

abstract class MapDrawing(id: Int)

object MapDrawing {

  implicit object MapDrawingFormat extends Format[MapDrawing] {

    override def reads(json: JsValue): JsResult[MapDrawing] = {
      (json \ "id").validate[Int].flatMap(id => {
        (json \ "type").validate[String].flatMap {
          case "rect" => {
            (json \ "top").validate[Double].flatMap(top => {
              (json \ "right").validate[Double].flatMap(right => {
                (json \ "left").validate[Double].flatMap(left => {
                  (json \ "bottom").validate[Double].map(bottom => {
                    Rect(id, top, right, left, bottom)
                  })
                })
              })
            })
          }
          case "circle" => {
            (json \ "radius").validate[Double].flatMap(radius => {
              (json \ "center").validate[Point].map(center => {
                Circle(id, radius, center)
              })
            })
          }
        }
      })
    }

    override def writes(o: MapDrawing): JsValue = JsObject(
      o match {
        case circle: Circle =>
          Seq("id" -> JsNumber(circle.circleId), "type" -> JsString("circle"), "radius" -> JsNumber(circle.radius), "center" -> Json.toJson(circle.center))

        case rect: Rect =>
          Seq("id" -> JsNumber(rect.rectId), "type" -> JsString("rect"), "top" -> JsNumber(rect.top), "bottom" -> JsNumber(rect.bottom), "left" -> JsNumber(rect.left), "right" -> JsNumber(rect.right))
        case _ => Seq()
      }
    )
  }
}

case class AddableObject(genericName: String,
                         mapDrawings: Array[MapDrawing],
                         description: String,
                         files: Array[String])

object AddableObject {
  implicit object AddableFormat extends Format[AddableObject] {

    override def writes(o: AddableObject): JsValue = JsObject(
      Seq("genericName" -> JsString(o.genericName))
    )

    override def reads(json: JsValue): JsResult[AddableObject] = {
      (json \ "genericName").validate[String].flatMap(genericName => {
        (json \ "mapDrawings").validate[Array[MapDrawing]].flatMap(mapDrawings => {
          (json \ "description").validate[String].flatMap(description => {
            (json \ "files").validate[Array[String]].map(
              files => AddableObject(genericName, mapDrawings, description, files)
            )
          })
        })
      })
    }

  }
}

@Singleton
class Application @Inject() (addableDao: AddableDao) extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def saveObject = Action { request =>

    val json = request.body.asJson.get
    addableDao.saveAddable(json.as[AddableObject])

    Ok("All good")
  }
}
