package io.mwielocha.differential

object Outcome {
  val empty: Outcome = Outcome(Nil)

  def apply(differences: Difference[_]*): Outcome =
    new Outcome(differences.toList)
}

case class Outcome(differences: List[Difference[_]]) {

  def ++(other: Outcome): Outcome =
    Outcome(differences ++ other.differences)

  val isEmpty: Boolean = differences.isEmpty

}
