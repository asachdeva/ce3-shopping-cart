package com.example.cart.modules

import cats.effect._
import com.example.cart.domain.product._
import io.circe.generic.auto._
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import org.http4s.ember.client.EmberClientBuilder

trait ProductResolver {
  def retrieveProduct(product: String): IO[Product]
}

class ProductResolverImpl(url: String) extends ProductResolver {
  implicit val productDecoder: EntityDecoder[IO, Product] = jsonOf[IO, Product]

  override def retrieveProduct(name: String): IO[Product] =
    EmberClientBuilder
      .default[IO]
      .build
      .use(client => client.expect(s"$url$name.json"))
}
