package simple

import org.scalajs.dom
import org.scalajs.dom.ext.{Ajax, KeyCode}
import org.scalajs.dom.html
import org.scalajs.dom.raw.Node

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object Client extends {

  @JSExport
  def main(container: html.Div): Node = {

    val inputBox = input.render
    val outputBox = div.render

    def update() = Ajax.post("/mongo/data", inputBox.value).foreach { xhr =>
      val data = upickle.default.read[Seq[String]](xhr.responseText)
      outputBox.innerHTML = ""
      for (entry <- data) {
        outputBox.appendChild(
          div(pre(entry)).render
        )
      }
    }

    inputBox.onkeyup = (e: dom.KeyboardEvent) => if (e.keyCode == KeyCode.Enter) update()
    inputBox.size = 150

    update()

    container.appendChild(
      div(
        h1("Oak"),
        div(b("Query: "), inputBox),
        outputBox
      ).render
    )
  }
}