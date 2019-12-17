package io.mwielocha.differential.instances

import io.mwielocha.differential.{Derived, Difference, Differential, Outcome}

trait Basic {

  implicit def catchAllDifferential[T]: Derived[Differential[T]] = Derived({
    case (left, right, _) if left == right => Outcome(Nil)
    case (left, right, path) => Outcome(List(Difference(left, right, path)))
  }: Differential[T])
}
