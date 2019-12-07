package edu.holycross.shot.commentary

import edu.holycross.shot.cite._
import scala.io.Source

import laika.api._
import laika.format._

import wvlet.log._
import wvlet.log.LogFormatter.SourceCodeLogFormatter


/** A commentary on passages of text.
*/
case class TextCommentary(comments: Map[CtsUrn, Vector[TextComment]]) //extends Commentary


object TextCommentary extends LogSupport {

  val transformer = Transformer.from(Markdown).to(HTML).build


  /** Build a [[TextCommentary]] from delimited text data,
  * with one comment per line.  Within comments, markdown is
  * converted to HTML.
  *
  * @param data Vector of delimited-text triples.
  */
  def apply(data: Vector[String]) : TextCommentary = {
    val comments = for (ln <- data) yield  {
      val columns = ln.split("#")
      if (columns.size < 3) {
        val msg = "Failed to make comment on line " + ln
        warn(msg)
        throw new Exception(msg)

      } else {
        val xformed = transformer.transform(columns(2))
        val commentaryText = xformed match {
          case Right(html) => {

            val header = "<div class=\"comment\">\n"
            val trail = "\t</div>"
            header + "\t" + html + "\n" + trail + "\n"
          }
          case _ => ln
        }
        TextComment(CtsUrn(columns(0)), columns(1), commentaryText)
      }
    }
    TextCommentary(comments.groupBy(_.urn))
  }

  /** Build a [[TextCommentary]] from a delimited-text file.
  *
  * @param commentaryFile CEX file with commentary in three columns.
  */
  def fromFile(commentaryFile: String) : TextCommentary = {
    val commentaryLines = Source.fromFile(commentaryFile).getLines.toVector
    TextCommentary(commentaryLines)
  }
}
