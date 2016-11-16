package dao

import javax.inject.Inject

import controllers.AddableObject
import org.joda.time.DateTime
import play.api.db.Database
import play.api.libs.json.Json

/**
  * Created by saarlane on 16/11/16.
  */
class AddableDao @Inject() (db: Database) {

  def saveAddable(addable: AddableObject): Unit = {
    db.withConnection(connection => {
      val statement = connection.createStatement()
      statement.executeUpdate("INSERT INTO findem.Object (genericName, mapDrawings, description, created) VALUES (\"" +
        addable.genericName + "\", \"" +
        Json.toJson(addable.mapDrawings.toList).toString().replaceAll("\"", "'") + "\", \"" +
        addable.description + "\", \"" +
        new DateTime().toString("yyyy-MM-dd HH:mm:ss") +
        "\")")
      connection.close()
    })
  }
}
