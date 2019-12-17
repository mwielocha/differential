package io.mwielocha.differential.scatatest

import java.time.Instant

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class DiffMatcherSpec extends AnyFlatSpec with Matchers with ShouldDiffMatcher {

  // this spec is suppose to fail, ignore if needed

  "DiffMatcher" should "yield a correct match result" in {

    case class Name(first: String, last: String)
    case class Address(street: String, number: Int)

    case class Specimen(name: Name, address: Address, age: Int, dates: Set[Instant])

    case class Specimens(category: String, sample: List[Specimen])

    val left = Specimens("Jims", List(
      Specimen(Name("Jim", "Beam"), Address("Churchil", 75), 22, Set.empty),
      Specimen(Name("Jim", "Beam"), Address("Washington", 75), 22, Set(Instant.now)),
      Specimen(Name("Jim", "Beam"), Address("Washington", 75), 22, Set.empty)
    ))

    val right = Specimens("John & Jim", List(
      Specimen(Name("John", "Doe"), Address("Washington", 73), 21, Set.empty),
      Specimen(Name("Jim", "Beam"), Address("Washington", 75), 22, Set(Instant.now))
    ))

    left shouldBeSameAs right
    left should beSameAs(right)

  }
}
