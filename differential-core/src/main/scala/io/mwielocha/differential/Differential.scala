package io.mwielocha.differential

trait Differential[T] {

  def compare(left: T, right: T, path: List[Segment] = Nil): Outcome

  def contramap(func: T => T): Derived[Differential[T]] = Derived({
    (left, right, path) =>
      compare(func(left), func(right), path)
  }: Differential[T])
}

object Differential {

  def compare[T](left: T, right: T)(implicit df: Differential[T]): Outcome =
    df.compare(left, right)
}
