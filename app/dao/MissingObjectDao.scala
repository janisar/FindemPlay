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

  val redis = new RedisClient(
    configuration.getString("redis.endpoint.url").get,
    configuration.getInt("redis.endpoint.port").get)

  def getMissingObjects: List[FindemObject] = {
    val result = redis.lrange("missingObject", 0, 100).get.map(option => {
      Json.parse(option.get).as[FindemObject]
    })

    result
  }

  def saveAddable(missingObject: String): Long = {
    redis.rpush("missingObject", missingObject).getOrElse(0)
  }
}
