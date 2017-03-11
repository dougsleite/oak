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

class MongoService(mongoQueryParser: MongoQueryParser) {

  // You will only need one instance of class MongoClient even with multiple concurrently executing asynchronous operations.
  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  val database: MongoDatabase = mongoClient.getDatabase("vm")
  val collection: MongoCollection[Document] = database.getCollection("letters")

  def executeQuery(query: String): Seq[String] = Try {
    val result: Future[Document] = collection.find(mongoQueryParser.parse(query)).first().head()
    List(Await.result(result, Duration(30, SECONDS)).toJson(new JsonWriterSettings(JsonMode.STRICT, true)))
  } match {
    case Success(value) => value
    case Failure(f) => List()
  }
}
