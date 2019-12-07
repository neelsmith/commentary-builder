package edu.holycross.shot.commentary

import edu.holycross.shot.cite._

/** A categorized comment on a citable object.
*/
trait Comment {
  /** Object being commented on.*/
  def urn: Urn

  /** A classifying value.*/
  def taxon: String

  /** Text of the comment.*/
  def comment: String
}
