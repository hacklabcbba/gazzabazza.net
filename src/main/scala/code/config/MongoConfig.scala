package code
package config

import net.liftweb._
import common._
import http._
import json._
import mongodb._
import util.Props
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB, MongoHost, MongoIdentifier}
import com.mongodb.{MongoClient, ServerAddress, Mongo}
import net.liftweb.util.Props
import java.net.URI

import com.mongodb.{DBAddress, MongoClient}

object MongoConfig extends Factory with Loggable {

  // configure your MongoMetaRecords to use this. See lib/RogueMetaRecord.scala.
  val defaultId = new FactoryMaker[MongoIdentifier](DefaultMongoIdentifier) {}

  def defineDb(id: MongoIdentifier, url: String) {

    val uri = new URI(url)

    val db = uri.getPath drop 1
    //println(uri.getHost)
    val server = new Mongo(new ServerAddress(uri.getHost, Props.getInt("mongo.default.port", 27017)))

    Option(uri.getUserInfo).map(_.split(":")) match {
      case Some(Array(user,pass)) =>
        MongoDB.defineDbAuth(id, server, db, user, pass)
      case _ =>
        MongoDB.defineDb(id, server, db)
    }
  }

  def init() = {
    Props.get("mongo.default.url") match {
      case Full(url) => defineDb(DefaultMongoIdentifier, url)
      case _ => init2()
    }
  }

  def init2() {
    /**
      * First checks for existence of mongo.default.url. If not found, then
      * checks for mongo.default.host, port, and name. Uses defaults if those
      * are not found.
      */
    val defaultDbAddress = Props.get("mongo.default.url")
      .map(url => new DBAddress(url))
      .openOr(new DBAddress(
        Props.get("mongo.default.host", "127.0.0.1"),
        Props.getInt("mongo.default.port", 27017),
        Props.get("mongo.default.name", "gazzabazza")
      ))

    /*
     * If mongo.default.user, and pwd are defined, configure Mongo using authentication.
     */
    (Props.get("mongo.default.user"), Props.get("mongo.default.pwd")) match {
      case (Full(user), Full(pwd)) =>
        MongoDB.defineDbAuth(
          DefaultMongoIdentifier,
          new MongoClient(defaultDbAddress),
          defaultDbAddress.getDBName,
          user,
          pwd
        )
        logger.info("MongoDB inited using authentication: %s".format(defaultDbAddress.toString))
      case _ =>
        MongoDB.defineDb(
          DefaultMongoIdentifier,
          new MongoClient(defaultDbAddress),
          defaultDbAddress.getDBName
        )
        logger.info("MongoDB inited: %s".format(defaultDbAddress.toString))
    }
  }
}

