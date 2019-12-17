# differential

Diff library with scalatest support.
Usefull for adjusting different types for a better match in specs.

You can for example adjust `Instant` to be truncated only to `HOURS`, this will impact all matches in a spec:

```scala

implicit val _  =
    Derived[Differential[Instant]]
      .contramap(_.truncatedTo(ChronoUnit.HOURS))
      
it should "find differences with custom implicit" in {

    case class Specimen(name: String, date: Instant)

    val left = Specimen("John", Instant.now)
    val right = Specimen("John", Instant.now)

    Differential.compare(left, right) shouldBe Outcome.empty
  }
```

With scalatest:

```scala
class DiffMatcherSpec extends AnyFlatSpec with Matchers with DiffMatcher {
  (...)
  val left = Specimens("Jims", List(
    Specimen(Name("Jim", "Beam"), Address("Churchil", 75), 22, Nil),
    Specimen(Name("Jim", "Beam"), Address("Washington", 75), 22, Seq(Instant.now)),
    Specimen(Name("Jim", "Beam"), Address("Washington", 75), 22, Nil)
  ))
  
  val right = Specimens("John & Jim", List(
    Specimen(Name("John", "Doe"), Address("Washington", 73), 21, Nil),
    Specimen(Name("Jim", "Beam"), Address("Washington", 75), 22, Seq(Instant.now))
  ))

  left should matchWithoutDifferences (right)
}
```
or with a `should` helper:

```scala
class DiffMatcherSpec extends AnyFlatSpec with Matchers with ShouldDiffMatcher {
  (...)
  left shouldMatchWithoutDifferences right
}
```

Should output:


```
Diff:
[info]   Specimens(
[info]     "Jims",
[info]     List(
[info]       Specimen(Name("Jim", "Beam"), Address("Churchil", 75), 22, List()),
[info]       Specimen(Name("Jim", "Beam"), Address("Washington", 75), 22, List(2019-12-12T00:49:27.058233Z)),
[info]       Specimen(Name("Jim", "Beam"), Address("Washington", 75), 22, List())
[info]     )
[info]   ) was not equal to
[info]   Specimens(
[info]     "John & Jim",
[info]     List(
[info]       Specimen(Name("John", "Doe"), Address("Washington", 73), 21, List()),
[info]       Specimen(Name("Jim", "Beam"), Address("Washington", 75), 22, List(2019-12-12T00:49:27.058553Z))
[info]     )
[info]   )
[info]
[info]   Details:
[info]   1. category:                 Jims was not equal to John & Jim
[info]   2. sample[0].name.first:     Jim was not equal to John
[info]   3. sample[0].name.last:      Beam was not equal to Doe
[info]   4. sample[0].address.street: Churchil was not equal to Washington
[info]   5. sample[0].address.number: 75 was not equal to 73
[info]   6. sample[0].age:            22 was not equal to 21
[info]   7. sample[1].dates[0]:       2019-12-12T00:49:27.058233Z was not equal to 2019-12-12T00:49:27.058553Z
[info]   8. sample[2]:                Specimen(Name(Jim,Beam),Address(Washington,75),22,List()) was not equal to [none] (DiffMatcherSpec.scala:29)
```
Also comes with console colors.
