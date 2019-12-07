// Build laika transformer:
import laika.api._
import laika.format._
val transformer = Transformer.from(Markdown).to(HTML).build


import scala.io.Source
import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._
import java.io.PrintWriter

// Default values for commentedPassages function:
// title of web page and src files to use for commentary and text corpus.
val title = "Livy 2.1 with commentary"
val fName = "src/docs/commentary.cex"
val textCex = "data/livy-selection.cex"
val preface = "preface.md"
val corpus =  CorpusSource.fromFile(textCex, cexHeader = true)

/** Read markdown from a file and convert it to HTML.
*
* @param fName Name of file with markdown content.
*/
def htmlFromMdFile(fName: String) : String = {
  val markdown = Source.fromFile(fName).getLines.toVector.mkString
  val xformed = transformer.transform(markdown)
  xformed match {
    case Right(html) => {
      html
    }
    case _ => ""
  }
}

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
          val header = "\t<div class=\"ohco2_commentBlock\" data-commentBlockLabel=\"" + columns(1) + "\">"
          val trail = "\t</div>"
          header + "\t" + html + "\n" + trail + "\n"
        }
        case _ => ln
      }
      (CtsUrn(columns(0)), commentaryText)
    }

  }
  comments.toVector.groupBy(_._1).toVector.map{ case (k,v) => (k, v.map(_._2).toVector) }
}

/** Construct a page of passages with commentary for all citable nodes in a Corpus.
*
* @param title Title of web page
* @param corpus Text corpus to display
* @param commentary Commentary to attach to citable nodes.
*/
def commentedPassages (title: String, prefaceFile: String, corpus: Corpus, commentary: Map[CtsUrn, Vector[String]]) : String = {
  val preface = Source.fromFile(prefaceFile).getLines.toVector.mkString("\n")
  val pageHeader = s"<html>\n<head>\n<title>${title}</title>\n" +
  "<link rel=\"stylesheet\" href=\"includes/cite_text_commentary.css\">\n" +
  "<link rel=\"stylesheet\" href=\"includes/latin213.css\">\n" +
	"<script type=\"text/javascript\" src=\"includes/jquery-3.4.1.min.js\"></script>\n" +
  "</head>\n<!--Include one non-ASCII character! α -->\n<body>\n" + preface + s"<h2>${title}</h2>\n\n"



  val pageTail = "\n</body>\n<script type=\"text/javascript\" src=\"includes/cite_text_commentary.js\"></script>\n</html>"

  val passages = for (n <- corpus.nodes) yield {
    val psgHeader = 	"\n<div class=\"ohco2_commentedPassage\">\n\t<p>" +
    n.text.replaceAll("\\+", "") + "</p>\n"
    val psgTail = "\n</div>\n"
    val psgComments = if (commentary.contains(n.urn)) {
      commentary(n.urn)
    } else {
      Vector("")
    }
    psgHeader + psgComments.mkString("\n\n") +  psgTail
  }
  pageHeader + passages.mkString("\n\n") + pageTail
}


val commentary = buildCommentary().toMap
val html =  commentedPassages(title, preface, corpus, commentary)

new PrintWriter("output.html"){write(html);close;}
