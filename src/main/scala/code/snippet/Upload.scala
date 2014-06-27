package code
package snippet


import net.liftweb._
import common._
import util._
import Helpers._
import net.liftweb.http.{FileParamHolder, SHtml}
import code.model.RandomImage
import com.mongodb.WriteConcern
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}
import com.mongodb.gridfs.GridFS

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
    MongoDB.use(DefaultMongoIdentifier) {
      db =>
        val fs = new GridFS(db)
        val mongoFile = fs.createFile(fp.fileStream)
        mongoFile.setFilename(nextFuncName)
        mongoFile.setContentType(fp.mimeType)
        mongoFile.save()
        rf.fileId(mongoFile.getFilename)
        RandomImage.save(rf, WriteConcern.NORMAL)
    }
  }

}
