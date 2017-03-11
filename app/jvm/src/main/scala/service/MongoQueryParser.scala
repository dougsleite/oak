package service

import org.json4s.JsonAST.JBool
import org.json4s.JsonAST.JDecimal
import org.json4s.JsonAST.JDouble
import org.json4s.JsonAST.JInt
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JString
import org.json4s.native.JsonMethods
import org.mongodb.scala.bson.collection.immutable.Document

/**
  * Simple Query for Mongo queries
  */
class MongoQueryParser {

  def parse(query: String): Document = {
    val json = JsonMethods.parse(query, useBigDecimalForDouble = true)
    val mongoQuery: List[Document] =
      for {
        JObject(child) <- json
        (fieldName, value) <- child
      } yield {
        value match {
          case JString(s) => Document(fieldName -> s)
          case JDouble(num) => Document(fieldName -> num)
          case JDecimal(num) => Document(fieldName -> num)
          case JInt(num) => Document(fieldName -> num.intValue())
          case JBool(bool) => Document(fieldName -> bool)
          case _ => throw new IllegalArgumentException(f"Error when processing field '$fieldName%s': Unsupported type: '$value'")
        }
      }
    mongoQuery.reduce(_ ++ _)
  }
}
