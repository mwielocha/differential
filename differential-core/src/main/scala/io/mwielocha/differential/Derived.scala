package io.mwielocha.differential

case class Derived[T](value: T)

object Derived {
  def apply[T: Derived]: Derived[T] =
    implicitly[Derived[T]]
}
