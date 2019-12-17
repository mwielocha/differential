package io.mwielocha.differential.scatatest

import io.mwielocha.differential.{Differential, Implicits, Outcome}
import org.scalatest.matchers.{MatchResult, Matcher}

trait DiffMatcher extends Implicits {

  def beSameAs[T: Differential](left: T): DiffMatcherImpl[T] = DiffMatcherImpl(left)

  @deprecated("use beSameAs instead", "0.2.4")
  def matchWithoutDifferences[T: Differential](left: T): DiffMatcherImpl[T] = DiffMatcherImpl(left)

  private[differential] case class DiffMatcherImpl[T: Differential](right: T) extends Matcher[T] {
    override def apply (left: T): MatchResult = {
      Differential.compare(left, right) match {
        case Outcome(Nil) =>
          MatchResult(matches = true, "", "")
        case Outcome(differences) =>
          val message = DiffFormatter.format(left, right, differences)
          MatchResult(matches = false, message, "")
      }
    }
  }
}
