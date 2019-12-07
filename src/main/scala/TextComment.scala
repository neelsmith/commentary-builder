package edu.holycross.shot.commentary

import edu.holycross.shot.cite._

/** A categorized comment on a passage of text.
*/
case class TextComment(urn: CtsUrn, taxon: String, comment: String) extends Comment
