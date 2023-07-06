package com.example.cart.services

import cats.effect.IO
import cats.implicits.toTraverseOps
import com.example.cart.domain.cart.CartItem
import com.example.cart.modules.InMemoryShoppingCart.{TaxRate, TotalPayableRate}
import com.example.cart.modules.{ShoppingCartResolver, _}

class ShoppingCartService(
    shoppingCartResolver: ShoppingCartResolver,
    productResolver: ProductResolver
) {
  val BigDecimalZero = BigDecimal(0.0)

  def add(item: CartItem): IO[Unit] = for {
    product <- productResolver.retrieveProduct(item.product.name.value)
    _ <- shoppingCartResolver.addItem(CartItem(product, item.quantity))
  } yield ()

  def add(products: List[CartItem]): IO[Unit] = products.traverse(add).void

  def get: IO[Seq[CartItem]] = shoppingCartResolver.get

  def subtotal: IO[BigDecimal] =
    get.map(f =>
      f.foldLeft(BigDecimalZero) { case (total, items) =>
        total + (items.quantity.value * items.product.price.amount)
      }.setScale(2, BigDecimal.RoundingMode.HALF_UP)
    )

  def taxPayable: IO[BigDecimal] = subtotal.map(_ * TaxRate)
  def totalPayable: IO[BigDecimal] = subtotal.map(_ * TotalPayableRate)

}
