package code
package lib

/**
 * Created with IntelliJ IDEA.
 * User: j2
 * Date: 16-01-14
 * Time: 09:11 AM
 * To change this template use File | Settings | File Templates.
 */

import net.liftweb.http.rest.RestHelper
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.util.Helpers._
import code.model._
import net.liftweb.http.StreamingResponse
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}
import com.mongodb.gridfs.{GridFSDBFile, GridFS}
import org.joda.time._
import com.foursquare.rogue.LiftRogue._
import net.liftweb.http.js.JsCmds.Reload
import javax.imageio.ImageIO
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import net.liftmodules.imaging.ImageResizer

object RandomImageApi extends RestHelper with Loggable with FileUploadHelper {
  serve {
    case "serving" :: "image" ::  imageFileId :: Nil Get    req => serveImage(imageFileId)
  }

  serve {
    case "serving" :: "thumbnail" :: imageFileId :: Nil Get    req => serveImageThumbnail(imageFileId)
  }
}

trait FileUploadHelper extends Loggable {

  def serveImage(fileId: String): LiftResponse = {
    //Find the file in the temp by file id
    RandomImage.find(fileId) match {
      case Full(randomImage) =>
        MongoDB.use(DefaultMongoIdentifier) (db => {
          val fs = new GridFS(db)
          fs.findOne(randomImage.fileId.get) match {
            case file: GridFSDBFile =>
              val fileName = randomImage.title.get.replace(" ","_")
              val headers = ("Content-type" -> file.getContentType) ::
                ("Content-length" -> file.getLength.toString) ::
                ("Content-disposition" -> (s"attachment; filename= ${fileName}")) :: Nil
              val stream = file.getInputStream

              StreamingResponse(
                stream,
                () => stream.close,
                file.getLength,
                headers, Nil, 200)
          }
        })
      case _ =>
        NotFoundResponse("")
    }
  }

  def serveImageThumbnail(fileId: String): LiftResponse = {
    //Find the file in the temp by file id
    RandomImage.find(fileId) match {
      case Full(randomImage) =>
        MongoDB.use(DefaultMongoIdentifier) (db => {
          val fs = new GridFS(db)
          fs.findOne(randomImage.fileId.get) match {
            case file: GridFSDBFile =>
              val fileName = randomImage.title.get.replace(" ","_")
              val headers = ("Content-type" -> file.getContentType) ::
                ("Content-length" -> file.getLength.toString) ::
                ("Content-disposition" -> (s"attachment; filename= ${fileName}")) :: Nil
              val img = ImageIO.read(file.getInputStream)
              val im = ImageResizer.max(Empty, img, 190, 105)
              val baos: ByteArrayOutputStream = new ByteArrayOutputStream()
              ImageIO.write(im,  file.getContentType.split("/")(1), baos)
              val b = baos.toByteArray
              baos.close()
              StreamingResponse(
                new ByteArrayInputStream(b),
                () => {},
                b.length,
                headers, Nil, 200)
          }
        })
      case _ =>
        NotFoundResponse("")
    }
  }
}

//ToDo define "real" allowed mime types, maybe add some extra validations

object AllowedMimeTypes extends Loggable {
  def unapply(req: Req): Option[Req] = {
    logger.info("req.uploadedFiles.map{_.mimeType) is %s".format(req.uploadedFiles.map{_.mimeType}))
    req.uploadedFiles.flatMap{_.mimeType match {
      case "image/bmp"            => Some(req)
      case "image/x-windows-bmp"  => Some(req)
      case "image/vnd.dwg"        => Some(req)
      case "image/gif"            => Some(req)
      case "image/x-icon"         => Some(req)
      case "image/jpeg"           => Some(req)
      case "image/pict"           => Some(req)
      case "image/png"            => Some(req)
      case "image/x-quicktime"    => Some(req)
      case "image/tiff"           => Some(req)
      case "image/x-tiff"         => Some(req)
      case _                      => Some(req)
    }}.headOption
  }
}
