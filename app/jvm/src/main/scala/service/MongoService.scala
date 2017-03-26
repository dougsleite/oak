package service

import java.util.concurrent.TimeUnit.SECONDS

import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.collection.immutable.Document

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.language.postfixOps
import scala.util.Failure
import scala.util.Success
import scala.util.Try

private object MongoService {
  val TIMEOUT = Duration(30, SECONDS)

  // You will only need one instance of class MongoClient even with multiple concurrently executing asynchronous operations.
  val MONGO_CLI: MongoClient = MongoClient("mongodb://localhost")
  val MONGO_DB: MongoDatabase = MONGO_CLI.getDatabase("vm")
  val MONGO_COLLECTION: MongoCollection[Document] = MONGO_DB.getCollection("letters")
}

class MongoService(mongoQueryParser: MongoQueryParser) {

  def executeQuery(query: String): Seq[String] = Try {
    val result2: Future[Seq[Document]] = MongoService.MONGO_COLLECTION.find(mongoQueryParser.parse(query)).toFuture()
    Await.result(result2, atMost = MongoService.TIMEOUT).map(doc => doc.toJson(new JsonWriterSettings(JsonMode.STRICT, true))).toList
  } match {
    case Success(value) => value
    case Failure(f) => List()
  }

}
