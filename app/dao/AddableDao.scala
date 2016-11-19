package dao


import com.redis.RedisClient
import model.LoginUser
import play.api.libs.json.Json

/**
  * Created by saarlane on 16/11/16.
  */
class AddableDao {

  val redis = new RedisClient("127.0.0.1", 6379)

  def validateLogin(loginUser: LoginUser): Boolean = {
    val result = redis.get("user:" + loginUser.email).getOrElse("")

    !result.isEmpty
  }

  def saveAddable(addable: String): Unit = {
    redis.lpush("addable", addable)
  }

  def register(loginUser: LoginUser): Boolean = {
    try {
      redis.set("user:" + loginUser.email, Json.toJson(loginUser))
      true
    } catch {
      case e: Exception => false
    }
  }
}
