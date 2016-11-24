package dao

import javax.inject.Inject

import com.redis.RedisClient
import model.LoginUser
import org.mindrot.jbcrypt.BCrypt
import play.api.Configuration
import play.api.libs.json.Json

/**
  * Created by saarlane on 24/11/16.
  */
class UserDao @Inject() (configuration: Configuration) {

  val redis = new RedisClient(
    configuration.getString("redis.endpoint.url").get,
    configuration.getInt("redis.endpoint.port").get)

  def validateLogin(loginUser: LoginUser): String = {
    val result = redis.get("user:" + loginUser.email).getOrElse("")

    if (result.isEmpty) {
      "Invalid email"
    } else {
      val user = Json.parse(result).as[LoginUser]

      if (!BCrypt.checkpw(loginUser.password, user.password)) {
        "Invalid password"
      } else {
        Json.stringify(Json.toJson(user))
      }
    }
  }

  def register(loginUser: LoginUser): Boolean = {
    try {

      val encrypted = encryptUser(loginUser)
      redis.set("user:" + loginUser.email, Json.toJson(encrypted))
      true
    } catch {
      case e: Exception => false
    }
  }

  def encryptUser(loginUser: LoginUser): LoginUser = {
    val hashedPw = BCrypt.hashpw(loginUser.password, loginUser.salt)
    new LoginUser(loginUser.userName, hashedPw, loginUser.email, loginUser.salt)
  }
}
