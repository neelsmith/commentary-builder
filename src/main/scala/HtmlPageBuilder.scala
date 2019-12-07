package edu.holycross.shot.commentary

import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import scala.io.Source


import wvlet.log._
import wvlet.log.LogFormatter.SourceCodeLogFormatter



object HtmlPageBuilder extends LogSupport {

  def html (
    title: String,
    prefaceFile: String,
    corpus: Corpus,
    commentary: TextCommentary) : String = {

      val preface = Source.fromFile(prefaceFile).getLines.toVector.mkString("\n")
      val pageHeader = s"<html>\n<head>\n<title>${title}</title>\n" +
        "<link rel=\"stylesheet\" href=\"includes/cite_text_commentary.css\">\n" +
        "<link rel=\"stylesheet\" href=\"includes/latin213.css\">\n" +
      	"<script type=\"text/javascript\" src=\"includes/jquery-3.4.1.min.js\"></script>\n" +
        "<meta charset=\"utf-8\"/>\n" +
        "</head>\n<body>\n" + preface + s"<h2>${title}</h2>\n\n"

      val pageTail = "\n</body>\n<script type=\"text/javascript\" src=\"includes/cite_text_commentary.js\"></script>\n</html>"

      val passages = for (n <- corpus.nodes) yield {
        val psgHeader = 	"\n<div class=\"ohco2_commentedPassage\">\n\t<p>" +
        n.text.replaceAll("\\+", "") + "</p>\n"
        val psgTail = "\n</div>\n"
        val psgComments = if (commentary.comments.keySet.contains(n.urn)) {
          // group them by taxon
          val groupedComments = commentary.comments(n.urn).groupBy(_.taxon).map{ case (u,comm) => (u,comm.map(_.comment)) }
          val formattedComments = for (tax <- groupedComments.keySet.toVector) yield {
            	val taxonHeader = "<div class=\"ohco2_commentBlock\" data-commentBlockLabel=\"" + tax + "\">\n"
              val taxonTail = "</div>\n"

              val taxonComments = groupedComments(tax)
              val formatted = taxonComments.map( c => {
                "<div class=\"comment\">\n" + c + "</div>\n"
              })
              taxonHeader + taxonComments.mkString("\n\n") + taxonTail

          }
          formattedComments
        } else {
          Vector("")
        }
        psgHeader + psgComments.mkString("\n\n") +  psgTail
      }
      pageHeader + passages.mkString("\n\n") + pageTail
    }
}
