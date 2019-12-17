package io.mwielocha.differential.scatatest

import io.mwielocha.differential.{Difference, Segment, Path}

object DiffFormatter {


  def format[T](left: T, right: T, differences: List[Difference[_]]): String = {

    val maxPath = differences.map(d => merge(d.path).length).max
    val maxPosition = differences.size.toString.length
    s"Diff:\n${pprint.apply(left)} was not equal to\n${pprint.apply(right)}\n" +
      fansi.Color.Reset("Details:") +
      differences.zipWithIndex.map { case (d, i) =>
        val depth = maxPath - merge(d.path).length
        val indent = " ".repeat(depth)
        val numberIndent = " ".repeat(maxPosition - (i + 1).toString.length + 1)
        Seq(
          fansi.Color.Yellow(s"${i + 1}.$numberIndent"),
          fansi.Color.Green(s"${merge(d.path)}: "),
          fansi.Color.Yellow(indent),
          fansi.Color.Yellow(s"${d.is.getOrElse("[none]")} "),
          fansi.Color.Red(s"was not equal to "),
          fansi.Color.Yellow(s"${d.shouldBe.getOrElse("[none]")}")
        ).mkString("")
      }.mkString("\n", "\n", "\n")
  }

  private def sformatt: Segment => String = {
    case Segment.Set => "[]"
    case Segment.Key(v) => s"[$v]"
    case Segment.Named(v) => v
    case Segment.Indexed(v) => s"[$v]"
  }

  private def merge(path: Path): String = path.map(sformatt).mkString(".").replaceAllLiterally(".[", "[")

}
