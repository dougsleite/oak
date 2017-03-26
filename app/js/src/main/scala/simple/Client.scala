package simple

import java.util.concurrent.TimeUnit.SECONDS

import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.html
import org.scalajs.dom.raw.Node

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object Client extends {

  @JSExport
  def main(container: html.Div): Node = {

    // Output Box
    val outputBox = div.render

    // Input Box
    val inputBox = input.render
    inputBox.size = 200
    inputBox.onkeyup = (e: dom.KeyboardEvent) => if (e.keyCode == KeyCode.Enter) updateResults(inputBox.value, outputBox)

    // Selection Box
    val cbox = select.render
    cbox.appendChild(option("5").render)
    cbox.appendChild(option("10").render)
    cbox.appendChild(option("50").render)
    cbox.appendChild(option("100").render)

    // TODO: Reuse code with Rx
    cbox.onchange = (e: dom.Event) => {
      outputBox.innerHTML = ""
      Ajax.post("/mongo/data", inputBox.value).foreach { xhr =>
        outputBox.innerHTML = ""
        upickle.default.read[Seq[String]](xhr.responseText)
          .take(cbox.value.toInt)
          .foreach(doc => outputBox.appendChild(div(pre(doc)).render))
      }
    }

    // Left Button
    val leftBtn = button("<-").render

    // Right Button
    val rightBtn = button("->").render

    // Render Page -----------------------------------------------------------------------------------------------------
    container.appendChild(
      div(
        h1("Oak"),
        div(b("Query: "), inputBox),
        div(leftBtn, rightBtn, cbox),
        outputBox
      ).render
    )
  }

  def updateResults(query: String, outputBox: html.Div): Unit =
    Ajax.post("/mongo/data", query).foreach { xhr =>
      outputBox.innerHTML = ""
      upickle.default.read[Seq[String]](xhr.responseText)
        .foreach(doc => outputBox.appendChild(div(pre(doc)).render))
    }

}