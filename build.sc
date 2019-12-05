// Build laika transformer:
import laika.api._
import laika.format._
val transformer = Transformer.from(Markdown).to(HTML).build



import scala.io.Source
import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._


val fName = "src/docs/commentary.cex"
/*

def buildCommentary(xformer: Transformer, corpus: Corpus, commentaryFile: String = fName): String = {
  val commentaryLines = Source.fromFile(commentaryFile).getLines.toVector
  for (ln <- commentaryLines) yield {
    val columns = ln.split("#")
    if (columns.size < 3) {
      // USE LOGGING HERE...
      println("Could not parse comment from line " + ln)
    } else {
      val comment : Either[ParserError, String] = transformer.transform(cols(2))
      println(comment)
    }

  }
}
*/


def buildCommentary(commentaryFile: String = fName, corpus: Corpus) = {// : Vector[String] = {
  val commentaryLines = Source.fromFile(commentaryFile).getLines.toVector
  val comments = for (ln <- commentaryLines) yield  {
    val columns = ln.split("#")
    if (columns.size < 3) {
      println("Failed to make comment on line " + ln )
    } else {
      val xformed = transformer.transform(columns(2))
      val commentaryText = xformed match {
        case Right(html) => {
          val header = "div class=\"ohco2_commentBlock\" data-commentBlockLabel=\"" + columns(1) + "\">"
          val trail = "</div>"
          header + html + trail
        }
        case _ => ln
      }
      commentaryText
    }

  }
  comments.toVector
}
val textCex = "livy.cex"
val livy =  CorpusSource.fromFile(textCex, cexHeader = true)
val commentary = buildCommentary(corpus = livy)
println("COMMENTARY size: " + commentary.size)
