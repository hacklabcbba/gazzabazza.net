package code.snippet

import net.liftweb._
import common._
import util._
import Helpers._
import net.liftweb.http.{FileParamHolder, SHtml}
import code.model.{RandomText, RandomImage}
import com.mongodb.WriteConcern
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}
import com.mongodb.gridfs.GridFS

/**
 * Created by j2 on 26-06-14.
 */
object WriteText {

  def render = {
    "textarea" #> SHtml.textarea("", saveText(_))
  }


  private def saveText(text: String) = {
    val rt = RandomText.createRecord
    rt.text(text)
    RandomText.save(rt, WriteConcern.NORMAL)
  }

}
