package code
package snippet

/**
 * Created by j2 on 26-06-14.
 */


import net.liftweb._
import common._
import util._
import Helpers._
import net.liftweb.http.{FileParamHolder, SHtml}
import code.model.{RandomText, RandomImage}
import com.mongodb.WriteConcern
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}

object Home {

  def render = {
    val image = RandomImage.findRandom((scala.math.random * RandomImage.count).toInt).dmap("")(_.id.get.toString)
    val text = RandomText.findRandom((scala.math.random * RandomImage.count).toInt).dmap("")(_.text.get)
    "data-name=random-image [src]" #> s"/serving/image/${image}" &
    "data-name=random-text *" #> text
  }

}
