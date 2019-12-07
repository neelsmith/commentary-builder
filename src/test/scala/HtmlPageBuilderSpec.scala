package edu.holycross.shot.commentary

import org.scalatest.FlatSpec

import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._

class HtmlPageBuilderSpec extends FlatSpec {


  val commentaryCex = "src/test/resources/livy-notes.cex"
  val commentary = TextCommentary.fromFile(commentaryCex)
  val corpusCex = "src/test/resources/livy-tiny.cex"
  val corpus =  CorpusSource.fromFile(corpusCex, cexHeader = true)


  "The HtmlPageBuilder object" should "compose an HTML string for commentary on a given corpus" in {
    val prefaceFile = "src/test/resources/livy-preface.html"
    val html =  HtmlPageBuilder.html(
      "Test corpus of Livy",
      prefaceFile,
      corpus,
      commentary)
    println(html)
    import java.io.PrintWriter
    new PrintWriter("src/test/resources/output.html"){write(html);close;}
  }
}
