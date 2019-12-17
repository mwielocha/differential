package io.mwielocha.differential

object Difference {

  def apply[T](is: T, shouldBe: T): Difference[T] =
    new Difference(Some(is), Some(shouldBe), Nil)

  def apply[T](is: T, shouldBe: T, path: List[Segment]): Difference[T] =
    new Difference(Some(is), Some(shouldBe), path)

}

case class Difference[T](is: Option[T], shouldBe: Option[T], path: List[Segment])
