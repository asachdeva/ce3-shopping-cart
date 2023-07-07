package com.example.cart.modules

import cats.effect._
import com.example.cart.domain.cart.{CartItem, Quantity}
import com.example.cart.domain.product._
import com.example.cart.modules.InMemoryShoppingCart.{TaxRate, TotalRate}
import com.example.cart.services.ShoppingCartService
import munit._
import org.http4s.server.Server

import ProductResolverMock._

class ShoppingCartServiceSpec extends CatsEffectSuite {

  val shoppingCartService: IO[ShoppingCartService] =
    InMemoryShoppingCart.instance.map(scr =>
      new ShoppingCartService(
        scr,
        new ProductResolverImpl("http://localhost:8081/")
      )
    )

  def toAddProduct(list: List[CartItem]): List[CartItem] = list.map(i =>
    CartItem(Product(i.product.name, i.product.price), i.quantity)
  )

  val myFixture: Fixture[Server] = ResourceSuiteLocalFixture(
    "my-server",
    ProductResolverMock.server
  )

  override def munitFixtures: Seq[Fixture[_]] = List(myFixture)

  // Add
  test("can add a single product to the shopping cart") {
    val productList = List(CartItem(cheerios, Quantity(1)))
    for {
      cart <- shoppingCartService
      _ <- cart.add(toAddProduct(productList))
      result <- cart.get
    } yield assertEquals(result, productList)
  }

  test("can add a list of the same products to shopping cart") {
    val productList = List(CartItem(cheerios, Quantity(10)))
    for {
      cart <- shoppingCartService
      _ <- cart.add(toAddProduct(productList))
      result <- cart.get
    } yield assertEquals(result, productList)
  }

  test("can add a list of different products to the shopping cart") {
    val productList = List(
      CartItem(cheerios, Quantity(10)),
      CartItem(cornflakes, Quantity(15)),
      CartItem(frosties, Quantity(7)),
      CartItem(foo, Quantity(1)),
      CartItem(bar, Quantity(10))
    )
    for {
      cart <- shoppingCartService
      _ <- cart.add(toAddProduct(productList))
      result <- cart.get
    } yield assertEquals(result, productList)
  }

  test("can add an empty list to the shopping cart") {
    val productList = List.empty
    for {
      cart <- shoppingCartService
      _ <- cart.add(toAddProduct(productList))
      result <- cart.get
    } yield assertEquals(result, productList)
  }

  test("can call add more than once for the correct value") {
    val productList =
      List(CartItem(cheerios, Quantity(5)))
    for {
      cart <- shoppingCartService
      _ <- cart.add(toAddProduct(productList))
      _ <- cart.add(toAddProduct(productList))
      result <- cart.get
    } yield assertEquals(result, List(CartItem(cheerios, Quantity(10))))
  }

  // Calculate cart subtotal
  test("can calculate the cart subtotal for a single product") {
    for {
      cart <- shoppingCartService
      _ <- cart.add(
        List(Product(cheerios.name, cheerios.price).cart(Quantity(1)))
      )
      subtotal <- cart.subtotal
    } yield assertEquals(subtotal, BigDecimal(1.2))
  }

  test("can calculate the cart subtotal for a list of the same product") {
    for {
      cart <- shoppingCartService
      _ <- cart.add(
        List(Product(cheerios.name, cheerios.price).cart(Quantity(20)))
      )
      subtotal <- cart.subtotal
    } yield assertEquals(subtotal, BigDecimal(24.0))
  }

  test("can calculate the cart subtotal for a list of different products") {
    for {
      cart <- shoppingCartService
      _ <- cart.add(
        List(
          CreateProductParam(
            NameParam(cheerios.name.value),
            PriceParam(cheerios.price.amount)
          ).toDomain.cart(Quantity(20)),
          CreateProductParam(
            NameParam(foo.name.value),
            PriceParam(foo.price.amount)
          ).toDomain.cart(Quantity(10)),
          CreateProductParam(
            NameParam(bar.name.value),
            PriceParam(bar.price.amount)
          ).toDomain.cart(Quantity(5))
        )
      )
      subtotal <- cart.subtotal
    } yield assertEquals(subtotal, BigDecimal(24.0 + 22.0 + 5.0))
  }

  test("can calculate the cart subtotal for an empty cart") {
    for {
      cart <- shoppingCartService
      _ <- cart.add(List.empty)
      subtotal <- cart.subtotal
    } yield assertEquals(subtotal, BigDecimal(0.0))
  }

  // Calculate tax
  test("can calculate the cart tax for a single product") {
    for {
      cart <- shoppingCartService
      _ <- cart.add(
        List(Product(cheerios.name, cheerios.price).cart(Quantity(1)))
      )
      tax <- cart.taxPayable
    } yield assertEquals(tax, TaxRate * 1.2)
  }

  test("can calculate the cart tax for a list of the same product") {
    for {
      cart <- shoppingCartService
      _ <- cart.add(
        List(Product(cheerios.name, cheerios.price).cart(Quantity(10)))
      )
      tax <- cart.taxPayable
    } yield assertEquals(tax, TaxRate * 12)
  }

  test("can calculate the cart tax for a list of different products") {
    for {
      cart <- shoppingCartService
      _ <- cart.add(
        List(
          Product(cheerios.name, cheerios.price).cart(Quantity(10)),
          Product(bar.name, bar.price).cart(Quantity(10)),
          Product(frosties.name, frosties.price).cart(Quantity(5))
        )
      )
      tax <- cart.taxPayable
    } yield assertEquals(tax, TaxRate * (1.2 * 10 + 10 + 1.8 * 5))
  }

  test("can calculate the cart tax for an empty cart") {
    for {
      cart <- shoppingCartService
      result <- cart.taxPayable
    } yield assertEquals(result, BigDecimal(0.0))

  }

  // Calculate total payable
  test("can calculate the cart total payable for a single product") {
    for {
      cart <- shoppingCartService
      _ <- cart.add(
        List(Product(cheerios.name, cheerios.price).cart(Quantity(1)))
      )
      tax <- cart.totalPayable
    } yield assertEquals(tax, TotalRate * 1.2)
  }

  test("can calculate the cart total payable for a list of the same product") {
    for {
      cart <- shoppingCartService
      _ <- cart.add(
        List(Product(cheerios.name, cheerios.price).cart(Quantity(10)))
      )
      tax <- cart.totalPayable
    } yield assertEquals(tax, TotalRate * 12)
  }

  test(
    "can calculate the cart total payable for a list of different products"
  ) {
    for {
      cart <- shoppingCartService
      _ <- cart.add(
        List(
          Product(cheerios.name, cheerios.price).cart(Quantity(10)),
          Product(bar.name, bar.price).cart(Quantity(10)),
          Product(frosties.name, frosties.price).cart(Quantity(5))
        )
      )
      tax <- cart.totalPayable
    } yield assertEquals(tax, TotalRate * (1.2 * 10 + 10 + 1.8 * 5))
  }

  test("can calculate the cart total payable for an empty cart") {
    for {
      cart <- shoppingCartService
      _ <- cart.add(List.empty[CartItem])
      tax <- cart.totalPayable
    } yield assertEquals(tax, BigDecimal(0.0))
  }

}
