package com.example.cart.modules

import cats.effect.IO
import com.comcast.ip4s.IpLiteralSyntax
import com.example.cart.domain.product._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.Method.GET
import org.http4s.Uri.Path.Root
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router

object ProductResolverMock {

  val cheerios = Product(Name("cheerios"), Price(1.2))
  val cornflakes = Product(Name("cornflakes"), Price(1.3))
  val frosties = Product(Name("frosties"), Price(1.8))
  val foo = Product(Name("foo"), Price(2.2))
  val bar = Product(Name("bar"), Price(1.0))

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "cheerios.json"   => Ok(cheerios.asJson)
    case GET -> Root / "cornflakes.json" => Ok(cornflakes.asJson)
    case GET -> Root / "foo.json"        => Ok(foo.asJson)
    case GET -> Root / "bar.json"        => Ok(bar.asJson)
    case GET -> Root / "frosties.json"   => Ok(frosties.asJson)
  }

  val httpApp = Router("" -> service).orNotFound

  val server = EmberServerBuilder
    .default[IO]
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8081")
    .withHttpApp(httpApp)
    .build
}
