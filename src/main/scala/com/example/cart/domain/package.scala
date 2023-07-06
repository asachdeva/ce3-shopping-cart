package com.example.cart

import cats.{Eq, Show}
import com.example.cart.domain.cart.Quantity
import com.example.cart.domain.product._

package object domain extends OrphanInstances

// instances for types we don't control
trait OrphanInstances {
  implicit val nameShow: Show[Name] = Show.fromToString
  implicit val priceShow: Show[Price] = Show.fromToString
  implicit val quantityShow: Show[Quantity] = Show.fromToString
  implicit val nameEq: Eq[Name] = Eq.by(_.value)
  implicit val priceEq: Eq[Price] = Eq.by(_.amount)
  implicit val quantityEq: Eq[Quantity] = Eq.by(_.value)
}
