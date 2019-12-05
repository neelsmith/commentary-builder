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


def buildCommentary(commentaryFile: String = fName) = {// : Map[CtsUrn, String]= {// : Vector[String] = {
  val commentaryLines = Source.fromFile(commentaryFile).getLines.toVector
  val comments = for (ln <- commentaryLines) yield  {
    val columns = ln.split("#")
    if (columns.size < 3) {
      val msg = "Failed to make comment on line " + ln
      println(msg)
      throw new Exception(msg)
    } else {
      val xformed = transformer.transform(columns(2))
      val commentaryText = xformed match {
        case Right(html) => {
          val header = "<div class=\"ohco2_commentBlock\" data-commentBlockLabel=\"" + columns(1) + "\">"
          val trail = "</div>"
          header + html + trail
        }
        case _ => ln
      }
      (CtsUrn(columns(0)), commentaryText)
    }

  }
  comments.toVector.groupBy(_._1).toVector.map{ case (k,v) => (k, v.map(_._2)) }.toMap
}

val commentary = buildCommentary()

val textCex = "livy-selection.cex"
val livy =  CorpusSource.fromFile(textCex, cexHeader = true)


def commentedPassages (title: String, corpus: Corpus, commentary: Map[CtsUrn, Vector[String]]) : String = {
  val pageHeader = s"<html><head><title>${title}</title>" +
  "<link rel=\"stylesheet\" href=\"includes/commentary.css\">" +
	"<script type=\"text/javascript\" src=\"includes/jquery-3.4.1.min.js\"></script>" +
  "</head>"
  val pageTail = "</html>"

  val passages = for (n <- corpus.nodes) yield {
    val psgHeader = 	"<div class=\"ohco2_commentedPassage\"><p>" + n.text + "</p>"
    val psgTail = "</div>"
    val psgComments = if (commentary.contains(n.urn)) {

    } else {
      ""
    }
    psgHeader + psgComments +  psgTail
  }
  pageHeader + passages.mkString("\n\n") + pageTail

}
