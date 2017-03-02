package service

import java.util.concurrent.TimeUnit.SECONDS

import org.bson.json.{JsonMode, JsonWriterSettings}
import org.mongodb.scala.bson.BsonObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class MongoService {

  // You will only need one instance of class MongoClient even with multiple concurrently executing asynchronous operations.
  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  val database: MongoDatabase = mongoClient.getDatabase("vm")
  val collection: MongoCollection[Document] = database.getCollection("letters")

  def executeQuery(query: String): Seq[String] = Try {
    val result: Future[Document] = collection.find(equal("_id", BsonObjectId(query))).first().head()
    List(Await.result(result, Duration(30, SECONDS)).toJson(new JsonWriterSettings(JsonMode.STRICT, true)))
  } match {
    case Success(value) => value
    case Failure(f) => List()
  }
}
