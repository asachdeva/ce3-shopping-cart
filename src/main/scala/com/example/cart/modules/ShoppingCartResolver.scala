package com.example.cart.modules

import cats.effect._
import cats.implicits._
import com.example.cart.domain.cart._

trait ShoppingCartResolver {
  def addItem(item: CartItem): IO[Unit]
  def add(items: List[CartItem]): IO[Unit]
  def get: IO[Seq[CartItem]]
}

class InMemoryShoppingCart(ref: Ref[IO, List[CartItem]])
    extends ShoppingCartResolver {

  private def coalesce(
      items: List[CartItem],
      item: CartItem
  ): List[CartItem] = {
    items match {
      case head :: tail if head.product === item.product =>
        head.copy(quantity =
          Quantity(head.quantity.value + item.quantity.value)
        ) :: tail
      case head :: tail => head :: coalesce(tail, item)
      case Nil          => List(item)
    }
  }

  def get: IO[List[CartItem]] = ref.get

  def addItem(addItem: CartItem): IO[Unit] =
    ref.update(coalesce(_, addItem)).void

  def add(items: List[CartItem]): IO[Unit] =
    items.traverse(addItem).void
}

object InMemoryShoppingCart {
  val TaxRate: BigDecimal = BigDecimal(0.125)
  val TotalRate: BigDecimal = TaxRate + 1

  val instance: IO[InMemoryShoppingCart] =
    Ref[IO].of(List.empty[CartItem]).map(new InMemoryShoppingCart(_))
}
