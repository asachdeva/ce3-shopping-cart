package com.example.cart.domain

import com.example.cart.domain.product.{Name, Product}
import derevo.cats._
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype

object cart {
  @derive(decoder, encoder, eqv, show)
  @newtype
  case class Quantity(value: Long)

  @derive(eqv, show)
  @newtype
  case class Cart(items: Map[Name, Quantity])

  object Cart {
    implicit val jsonEncoder: Encoder[Cart] =
      Encoder.forProduct1("items")(_.items)

    implicit val jsonDecoder: Decoder[Cart] =
      Decoder.forProduct1("items")(Cart.apply)
  }

  @derive(decoder, encoder, eqv, show)
  case class CartItem(product: Product, quantity: Quantity)

}
