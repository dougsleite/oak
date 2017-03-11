package service

import org.mongodb.scala.bson.collection.immutable.Document
import org.scalatest.FunSuite
import org.scalatest.Inside
import org.scalatest.Inspectors
import org.scalatest.Matchers
import org.scalatest.OptionValues

abstract class UnitSpec extends FunSuite
  with Matchers
  with OptionValues
  with Inside
  with Inspectors

/**
  * Tests for `MongoQueryParser`.
  */
class MongoQueryParserTest extends UnitSpec {

  trait Fixture {
    val parser = new MongoQueryParser()
  }

  test("Parse query") {
    new Fixture {

      val query: String =
        """
          |{
          |"name" : "The Lord of The Rings",
          |"author" : "J. R. R. Tolkien",
          |"isHardcover" : true,
          |"pages" : 1178,
          |"price" : 13.60,
          |"currency" : "GBP"
          |}
        """.stripMargin

      val expected: String =
        """{
          |"name" : "The Lord of The Rings",
          |"author" : "J. R. R. Tolkien",
          |"isHardcover" : true,
          |"pages" : 1178,
          |"price" : { "$numberDecimal" : "13.60" },
          |"currency" : "GBP"
          |}
        """.stripMargin

      val document: Document = parser.parse(query)
      assert(normalize(document.toJson()) === normalize(expected))
    }
  }

  private def normalize(s: String): String = s.split('\n').map(_.trim.filter(_ != ' ')).mkString

}