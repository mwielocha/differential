package io.mwielocha.differential

import java.time.Instant
import java.time.temporal.ChronoUnit

import org.scalatest.matchers.should.Matchers
import io.mwielocha.differential.syntax._
import org.scalatest.flatspec.AnyFlatSpec

class DifferentialSpec extends AnyFlatSpec with Matchers {

  implicit val _  =
    Derived[Differential[Instant]]
      .contramap(_.truncatedTo(ChronoUnit.HOURS))

  "Differential" should "find differences in simple case class" in {

    case class t(name: String, age: Int)

    val left = t("John", 21)
    val right = t("Jim", 21)

    Differential.compare(left, right) shouldBe Outcome(
      List(
        Difference("John", "Jim",
          List(Segment.Named("name")
          )
        )
      )
    )
  }

  it should "find differences in complex case class" in {

    case class Name(first: String, last: String)
    case class t(name: Name, age: Int)

    val left = t(Name("John", "Smith"), 21)
    val right = t(Name("John", "Malkovitch"), 21)

    Differential.compare(left, right) shouldBe Outcome(
      List(
        Difference(
          "Smith",
          "Malkovitch",
          List(
            Segment.Named("name"),
            Segment.Named("last")
          )
        )
      )
    )
  }

  it should "find differences with custom implicit" in {

    case class t(name: String, date: Instant)

    val left = t("John", Instant.now)
    val right = t("John", Instant.now)

    Differential.compare(left, right) shouldBe Outcome.empty
  }

  it should "find differences in a sequence / list / vector of case classes" in {

    case class t(names: List[String], numbers: Vector[Int])

    val left = Seq(t(List("John", "Doe"), Vector(21)))
    val right = Seq(t(List("John"), Vector(22)))

    Differential.compare(left, right) shouldBe Outcome(
      Difference(Some("Doe"), None, List(
        Segment.Indexed(0),
        Segment.Named("names"),
        Segment.Indexed(1)
      )),
      Difference(21, 22, List(Segment.Indexed(0), Segment.Named("numbers"), Segment.Indexed(0)))
    )
  }

  it should "find differences in a set of case classes" in {

    case class t(names: List[String], age: Int)

    val left = Set(t(List("John", "Doe"), 21))
    val right = Set(t(List("John"), 22))

    Differential.compare(left, right) shouldBe Outcome(
      Difference(Some(t(List("John", "Doe"), 21)), None, List(Segment.Set)),
      Difference(None, Some(t(List("John"), 22)), List(Segment.Set)),
    )
  }

  it should "find differences in a map of case classes" in {

    case class t(names: List[String], age: Int)

    val left = Map("John" -> t(List("John", "Doe"), 21))
    val right = Map("John" -> t(List("John"), 21))

    Differential.compare(left, right) shouldBe Outcome(
      Difference(Some("Doe"), None, List(
        Segment.Key("John"),
        Segment.Named("names"),
        Segment.Indexed(1)
      )),
    )
  }

  it should "find differences in a case class with type parameter" in {

    case class t(name: String, age: Int)

    implicit val _ =
      Derived[Differential[t]]
        .contramap(_.copy(age = 0))

    val left = t("John", 21)
    val right = t("Jim", 22)

    Differential.compare(left, right) shouldBe Outcome(List(Difference("John", "Jim", List(Segment.Named("name")))))

  }

  it should "find differences in a case class with optional parameter" in {

    case class Name(first: String, last: String)

    case class t(name: Option[Name], age: Int)

    val left = t(Some(Name("John", "Smith")), 22)
    val right = t(Some(Name("Jim", "Smith")), 22)

    Differential.compare(left, right) shouldBe Outcome(
      List(
        Difference("John", "Jim",
          List(
            Segment.Named("name"),
            Segment.Named("first")
          )
        )
      )
    )
  }

  it should "find differences in a case class with either (left/right) parameter" in {

    case class Name(first: String, last: String)

    case class t(name: Either[String, Name], age: Int)

    val left = t(Right(Name("John", "Smith")), 22)
    val right = t(Left("John"), 22)

    val expected = Outcome(
      List(
        Difference(
          Right(Name("John", "Smith")),
          Left("John"),
          List(
            Segment.Named("name"
            )
          )
        )
      )
    )

    Differential.compare(left, right) shouldBe expected
  }

  it should "find differences in a case class with either (both right) parameter" in {

    case class Name(first: String, last: String)

    case class t(name: Either[String, Name], age: Int)

    val left = t(Right(Name("John", "Doe")), 22)
    val right = t(Right(Name("John", "Smith")), 22)

    val expected = Outcome(
      List(
        Difference(
          "Doe",
          "Smith",
          List(
            Segment.Named("name"),
            Segment.Named("last")
          )
        )
      )
    )

    Differential.compare(left, right) shouldBe expected
  }
}
