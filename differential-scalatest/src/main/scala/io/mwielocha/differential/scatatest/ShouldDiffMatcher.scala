package io.mwielocha.differential.scatatest

import io.mwielocha.differential.Differential
import org.scalactic.source.Position
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers

trait ShouldDiffMatcher extends DiffMatcher {
  requires: Matchers =>

  implicit class ShouldDiffMatcherImpl[T](right: T) {

    def shouldBeSameAs(left: T)(implicit differential: Differential[T], pos: Position): Assertion =
      right should beSameAs(left)

    @deprecated("use shouldBeSameAs instead", "0.2.4")
    def shouldMatchWithoutDifferences(left: T)(implicit differential: Differential[T], pos: Position): Assertion =
      right should matchWithoutDifferences(left)
  }
}
