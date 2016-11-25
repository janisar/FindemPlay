package dao


import javax.inject.Inject

import com.redis.RedisClient
import model.{FindemObject, LoginUser}
import org.mindrot.jbcrypt.BCrypt
import play.api.Configuration
import play.api.libs.json.Json

/**
  * Created by saarlane on 16/11/16.
  */
class MissingObjectDao @Inject()(configuration: Configuration) {

  val MISSING_OBJECT_INDEX = "missingObject"

  val redis = new RedisClient(
    configuration.getString("redis.endpoint.url").get,
    configuration.getInt("redis.endpoint.port").get)

  def updateFindemObject(missingObject: FindemObject, index: Int): Unit = {
    redis.lset(MISSING_OBJECT_INDEX, index - 1, Json.toJson(missingObject).toString())
  }


  def getById(objectId: Long): FindemObject = {
    val result = Json.parse(redis.lindex(MISSING_OBJECT_INDEX, objectId.toInt - 1).get).as[FindemObject]

    result
  }

  def getMissingObjects(): List[FindemObject] = {
    val result = redis.lrange(MISSING_OBJECT_INDEX, 0, 100).get.map(option => {
      Json.parse(option.get).as[FindemObject]
    })

    result
  }

  def saveAddable(missingObject: String): Long = {
    redis.rpush(MISSING_OBJECT_INDEX, missingObject).getOrElse(0)
  }
}
