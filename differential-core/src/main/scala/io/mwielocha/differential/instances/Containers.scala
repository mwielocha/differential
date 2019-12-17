package io.mwielocha.differential.instances

import io.mwielocha.differential.{Derived, Difference, Differential, Outcome, Path, Segment}

import scala.annotation.tailrec
import scala.collection.{TraversableLike, breakOut}

trait Containers extends Basic {

  @tailrec
  private def traverse[L[_], T](
    left: L[T],
    right: L[T],
    path: Path,
    depth: Int = 0,
  )(outcome: Outcome)(implicit
    ev: L[T] <:< TraversableLike[T, L[T]],
    df: Derived[Differential[T]],
  ): Outcome = {
    if(left.isEmpty && right.isEmpty) outcome
    else if(left.isEmpty && right.nonEmpty) traverse(left, right.tail, path, depth + 1)(
      outcome ++
        Outcome(Difference(
          None, right.headOption,
          path :+ Segment.Indexed(depth)
        ))
    ) else if (left.nonEmpty && right.isEmpty) traverse(left.tail, right, path, depth + 1)(
      outcome ++
        Outcome(Difference(
          left.headOption,
          None, path :+ Segment.Indexed(depth)
        ))
    ) else traverse(left.tail, right.tail, path, depth + 1) {
      outcome ++
        df.value.compare(
          left.head,
          right.head,
          path :+ Segment.Indexed(depth)
        )
    }
  }

  implicit def traversableLikeDifferential[L[_], T](
    implicit
    ev: L[T] <:< TraversableLike[T, L[T]],
    df: Derived[Differential[T]]
  ): Derived[Differential[L[T]]] = Derived({
    traverse(_, _, _)(
      Outcome.empty
    )
  }: Differential[L[T]])

  implicit def setDifferential[T](
    implicit d: Derived[Differential[T]]
  ): Derived[Differential[Set[T]]] = Derived({
    (left, right, path) =>

      val dl: List[Difference[_]] =
        left.diff(right).map {
          x =>
            Difference(
              Some(x),
              None,
              path :+ Segment.Set
            )
        } (breakOut)

      val dr: List[Difference[_]] =
        right.diff(left).map {
          x =>
            Difference(
              None,
              Some(x),
              path :+ Segment.Set
            )
        }(breakOut)

      Outcome(dl ++ dr)
  }: Differential[Set[T]])

  implicit def mapDifferential[K, V](
    implicit differential: Derived[Differential[V]]
  ): Derived[Differential[Map[K, V]]] =
    Derived({
      (left, right, path) =>
        implicit val compare: (V, V, Path) => Outcome =
          differential
            .value
            .compare

        val dleft = (for {
          (lk, lv) <- left
          outcome = right.get(lk) match {
            case Some(rv) => compare(lv, rv, path :+ Segment.Key(lk))
            case None => Outcome(Difference(lv, None, path :+ Segment.Key(lk)))
          }
        } yield outcome).foldLeft(Outcome.empty)(_ ++ _)

        val dright = (for {
          (rk, rv) <- right
          outcome = left.get(rk) match {
            case Some(_) => Outcome.empty // we already have that one
            case None => Outcome(Difference(None, rv, path :+ Segment.Key(rk)))
          }
        } yield outcome).foldLeft(Outcome.empty)(_ ++ _)

        dleft ++ dright
    }: Differential[Map[K, V]])

  implicit def optionDifferential[T](
    implicit differential: Derived[Differential[T]]
  ): Derived[Differential[Option[T]]] =
    Derived({
      case (None, None, _) =>
          Outcome.empty
      case (Some(left), Some(right), path) =>
        differential
          .value
          .compare(left, right, path)
      case (sl@Some(_), None, path) =>
        Outcome(Difference(sl, None, path))
      case (None, sr@Some(_), path) =>
        Outcome(Difference(None, sr, path))
    }: Differential[Option[T]])

  implicit def eitherDifferential[L, R](
    implicit
    dl: Derived[Differential[L]],
    dr: Derived[Differential[R]]
  ): Derived[Differential[Either[L, R]]] =
    Derived({
      case (Right(lr), Right(rr), path) =>
        implicitly[Derived[Differential[R]]]
          .value.compare(lr, rr, path)
      case (Left(ll), Left(rl), path) =>
        implicitly[Derived[Differential[L]]]
          .value.compare(ll, rl, path)
      case (l@Left(_), r@Right(_), path) =>
        Outcome(Difference(l, r, path))
      case (l@Right(_), r@Left(_), path) =>
        Outcome(Difference(l, r, path))
    }: Differential[Either[L, R]])
}
