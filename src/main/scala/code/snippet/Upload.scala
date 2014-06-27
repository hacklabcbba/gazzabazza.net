package code
package snippet


import net.liftweb._
import common._
import util._
import Helpers._
import net.liftweb.http.{FileParamHolder, SHtml}
import code.model.RandomImage
import com.mongodb.WriteConcern

/**
 * Created by j2 on 26-06-14.
 */
object Upload {

  def render = {
    "type=file" #> SHtml.fileUpload(fp => saveFile(fp))
  }


  private def saveFile(fp: FileParamHolder) = {
    val rf = RandomImage.createRecord
    rf.title(fp.fileName)
    RandomImage.save(rf, WriteConcern.NORMAL)
    println(fp.fileName)
  }

}
