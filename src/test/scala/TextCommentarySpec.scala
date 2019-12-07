package edu.holycross.shot.commentary


import org.scalatest.FlatSpec

import edu.holycross.shot.cite._

class TextCommentarySpec extends FlatSpec {

  "The TextCommentary object" should "build a TextCommentary from CEX source" in {
    val cexFile = "src/test/resources/livy-notes.cex"
    val commentary = TextCommentary.fromFile(cexFile)

    val expectedComments = 3
    assert(commentary.comments.size == expectedComments)
  }
}
