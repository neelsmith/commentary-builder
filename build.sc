// Build laika transformer:
import laika.api._
import laika.format._
val transformer = Transformer.from(Markdown).to(HTML).build

val fName = "src/docs/commentary.cex"
val textCex = "livy-tiny.cex"
val livy =  CorpusSource.fromFile(textCex, cexHeader = true)



import scala.io.Source
import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._

/** Match leaf-node URNs with comments.
*
* @param commentaryFile CEX file with commentary in three columns.
*/
def buildCommentary(commentaryFile: String = fName) : Vector[(CtsUrn, Vector[String])] = {
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
  comments.toVector.groupBy(_._1).toVector.map{ case (k,v) => (k, v.map(_._2).toVector) }
}

val commentary = buildCommentary().toMap



def commentedPassages (title: String, corpus: Corpus, commentary: Map[CtsUrn, Vector[String]]) : String = {
  val pageHeader = s"<html><head><title>${title}</title>" +
  "<link rel=\"stylesheet\" href=\"includes/commentary.css\">" +
	"<script type=\"text/javascript\" src=\"includes/jquery-3.4.1.min.js\"></script>" +
  "</head>"
  val pageTail = "<script type=\"text/javascript\" src=\"cite_text_commentary.js\"></script></html>"

  val passages = for (n <- corpus.nodes) yield {
    val psgHeader = 	"<div class=\"ohco2_commentedPassage\"><p>" + n.text + "</p>"
    val psgTail = "</div>"
    val psgComments = if (commentary.contains(n.urn)) {
      commentary(n.urn).map(_.replaceAll("+", ""))
    } else {
      Vector("")
    }
    psgHeader + psgComments.mkString("\n\n") +  psgTail
  }
  pageHeader + passages.mkString("\n\n") + pageTail
}
