package com.example.cart.domain

import com.example.cart.domain.cart._
import derevo.cats._
import derevo.circe.magnolia._
import derevo.derive
import io.estatico.newtype.macros.newtype

object product {
  @derive(decoder, encoder, eqv, show)
  @newtype
  case class Price(amount: Double)

  @derive(decoder, encoder, keyDecoder, keyEncoder, eqv, show)
  @newtype
  case class Name(value: String)

  @derive(decoder, encoder, eqv, show)
  case class Product(
      name: Name,
      price: Price
  ) {
    def cart(q: Quantity): CartItem =
      CartItem(this, q)
  }

  // ----- Create item (line item for shopping cart) ------
  @derive(decoder, encoder, show)
  @newtype
  case class NameParam(value: String)

  @derive(decoder, encoder, show)
  @newtype
  case class PriceParam(amount: Double)

  @derive(decoder, encoder, show)
  case class CreateProductParam(
      name: NameParam,
      price: PriceParam
  ) {
    def toDomain: Product =
      Product(
        Name(name.value),
        Price(price.amount)
      )
  }

  // ----- Update product ------

  @derive(decoder, encoder)
  case class UpdateProductParam(
      price: PriceParam
  ) {
    def toDomain: UpdateProduct =
      UpdateProduct(
        Price(price.amount)
      )
  }

  @derive(decoder, encoder)
  case class UpdateProduct(
      price: Price
  )

}
