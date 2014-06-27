package code.model


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
class RandomImage private () extends MongoRecord[RandomImage] with ObjectIdPk[RandomImage] {
  def meta = RandomImage

  object title extends StringField(this, 256) {
    override def defaultValue = "Untitled"
  }

  object random extends DoubleField(this) {
    override def defaultValue = scala.math.random * RandomText.findAll.size
  }

  object fileId extends StringField(this, 50)
}

object RandomImage extends RandomImage with MongoMetaRecord[RandomImage] {
  def findRandom(random: Double): Box[RandomImage] = {
    RandomImage.skip((scala.math.random * RandomImage.count).toInt).fetch(1) headOption match {
      case None => findRandom((scala.math.random * RandomImage.count).toInt)
      case other => other
    }
  }
}
