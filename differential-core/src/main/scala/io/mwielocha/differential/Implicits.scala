package io.mwielocha.differential

import io.mwielocha.differential.instances.Generic

trait Implicits extends Generic {

  implicit def convertd[T](implicit dd: Derived[Differential[T]]): Differential[T] = dd.value

  implicit def dunwrap[T](dd: Derived[Differential[T]]): Differential[T] = dd.value
}

object Implicits extends Implicits
