package io.mwielocha.differential

sealed trait Segment

object Segment {

  case object Set extends Segment

  case class Key[T] (key: T) extends Segment

  case class Named (name: String) extends Segment

  case class Indexed (index: Int) extends Segment

}
