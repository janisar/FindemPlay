package model

import play.api.libs.json._

/**
  * Created by saarlane on 19/11/16.
  */
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
