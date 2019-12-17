package io.mwielocha.differential.instances

import io.mwielocha.differential.{Derived, Differential, Outcome, Segment}
import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Witness}
import shapeless.labelled.{FieldType, field}

trait Generic extends Containers {

  implicit val hnilDifferential: Derived[Differential[HNil]] = Derived({
    (_, _, _) =>
      Outcome.empty
  }: Differential[HNil])

  implicit def hlistDifferential[K, H, T <: HList](
    implicit dh: Derived[Differential[H]],
    df: Derived[Differential[T]],
    wk: Witness.Aux[K],
    ev: K <:< Symbol
  ): Derived[Differential[FieldType[K, H] :: T]] = Derived({
    (left, right, path) =>
      field[K](dh.value.compare(left.head, right.head, path :+ Segment.Named(wk.value.name))) ++
        df.value.compare(left.tail, right.tail, path)
  }: Differential[FieldType[K, H] :: T])


  implicit def genericDifferential[T, L <: HList](
    implicit
    g: LabelledGeneric.Aux[T, L],
    r: Lazy[Derived[Differential[L]]]
  ): Derived[Differential[T]] = Derived({
    (left, right, path) =>
      r.value.value.compare(
        g.to(left),
        g.to(right),
        path
      )
  }: Differential[T])
}
