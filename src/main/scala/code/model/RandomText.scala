package code.model

/**
 * Created by j2 on 26-06-14.
 */
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field._
import com.mongodb.WriteConcern
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.common._
import net.liftweb.http.S
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonAST.JArray
import net.liftweb.json.JsonAST.JField
import net.liftweb.json.JsonAST.JBool
import net.liftweb.json.JsonAST.JString
import net.liftweb.mongodb
import com.foursquare.rogue.LiftRogue._

/**
 * Created by j2 on 26-06-14.
 */
class RandomText private () extends MongoRecord[RandomText] with ObjectIdPk[RandomText] {
  def meta = RandomText

  object text extends StringField(this, 256) {
    override def defaultValue = "No text"
  }

  object random extends DoubleField(this) {
    override def defaultValue = scala.math.random * RandomText.findAll.size
  }

}

object RandomText extends RandomText with MongoMetaRecord[RandomText] {
  def findRandom(random: Double): Box[RandomText] = {
    RandomText skip((scala.math.random * RandomImage.count).toInt) fetch(1) headOption match {
      case None => findRandom((scala.math.random * RandomImage.count).toInt)
      case other => other
    }
  }
}

