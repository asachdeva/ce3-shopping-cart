package com.example

import cats._
import cats.implicits._

object Cats

sealed trait Json
final case class JsObject(get: Map[String, Json]) extends Json
final case class JsString(get: String) extends Json
final case class JsNumber(get: Double) extends Json

case object JsNull extends Json

// 1. The Typeclass as in Trait
trait JsonWriter[A] {
  def write(value: A): Json
}

case class Person(name: String, email: String, age: Double)

//2.  The Instances -- implicit vals
object JsonWriterInstances {
  implicit val stringWriter: JsonWriter[String] = (value: String) =>
    JsString(value)

  implicit val numberWriter: JsonWriter[Double] = (value: Double) =>
    JsNumber(value)

  implicit val personWriter: JsonWriter[Person] =
    (value: Person) =>
      JsObject(
        Map(
          "name" -> JsString(value.name),
          "email" -> JsString(value.email),
          "age" -> JsNumber(value.age)
        )
      )

  implicit def optionWriter[A](implicit
      writer: JsonWriter[A]
  ): JsonWriter[Option[A]] =
    new JsonWriter[Option[A]] {
      def write(option: Option[A]): Json = option match {
        case None         => JsNull
        case Some(aValue) => writer.write(aValue)
      }
    }
}

// 3. The TC use
// Interface Objects -- implicit params
object Json {
  def toJson[A](value: A)(implicit w: JsonWriter[A]): Json = w.write(value)
}

// Interface Syntax as in Extension Methods -- implicit classes
object JsonSyntax {
  implicit class JsonWriterOps[A](value: A) {
    def toJson(implicit w: JsonWriter[A]): Json = w.write(value)
  }
}

final case class Cat(name: String, age: Int, color: String)

final case class Box[A](value: A)

trait Printable[A] { self =>
  def format(value: A): String

  def contramap[B](func: B => A): Printable[B] =
    new Printable[B] {
      def format(value: B): String = self.format(func(value))
    }
}

object PrintableInstances {
  implicit val stringPrintable: Printable[String] = new Printable[String] {
    def format(value: String): String = s"'${value}'"
  }

  implicit val intPrintable: Printable[Int] = new Printable[Int] {
    def format(value: Int): String = value.toString
  }

  implicit val booleanPrintable: Printable[Boolean] =
    new Printable[Boolean] {
      def format(value: Boolean): String =
        if (value) "yes" else "no"
    }

  implicit def boxPrintable[A](implicit
      printable: Printable[A]
  ): Printable[Box[A]] =
    new Printable[Box[A]] {
      def format(box: Box[A]): String = printable.format(box.value)
    }

  implicit def boxPrintableCMap[A](implicit
      p: Printable[A]
  ): Printable[Box[A]] =
    p.contramap[Box[A]](_.value)

  implicit val catPrintable: Printable[Cat] = new Printable[Cat] {
    def format(cat: Cat): String = {
      val name = Printable.format(cat.name)
      val age = Printable.format(cat.age)
      val color = Printable.format(cat.color)
      s"$name is a $age year-old $color cat"
    }

  }
  def parseInt(str: String): Option[Int] =
    scala.util.Try(str.toInt).toOption

  def divide(a: Int, b: Int): Option[Int] =
    if (b == 0) None else Some(a / b)

  def sumSquares[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] =
    for {
      x <- a
      y <- b
    } yield x * x + y * y

  def factorial(n: BigInt): BigInt =
    if (n == 1) n else n * factorial(n - 1)

  def factorial2(n: BigInt): Eval[BigInt] =
    if (n == 1) {
      Eval.now(n)
    } else {
      Eval.defer(factorial(n - 1).map(_ * n))
    }

  object Printable {
    def format[A](value: A)(implicit printable: Printable[A]): String =
      printable.format(value)
    def print[A](value: A)(implicit printable: Printable[A]): Unit = println(
      format(value)
    )
  }

//  // Interface Syntax as in Extension Methods -- implicit classes
//  object JsonSyntax {
//    implicit class JsonWriterOps[A](value: A) {
//      def toJson(implicit w: JsonWriter[A]): Json = w.write(value)
//    }
//  }

  object PrintableSyntax {
    implicit class PrintableOps[A](value: A) {
      def format(implicit p: Printable[A]): String = p.format(value)
      def print(implicit p: Printable[A]): Unit = println(format(p))
    }
  }

  val applicative = Applicative[List]

  val numbers = List.range(1, 11)
  val squares = numbers.map(x => x * x)

  val sumOfSquares = applicative.ap(squares)(numbers)

  trait Semigroup[A] {
    def combine(x: A, y: A): A
  }

  trait Monoid[A] extends Semigroup[A] {
    def empty: A
  }

  object Monoid {
    def apply[A](implicit monoid: Monoid[A]) =
      monoid
  }

  1.pure[List]
  1.pure[Option]

  def combineAll[A: Monoid](as: List[A]): A =
    as.foldLeft(Monoid[A].empty)(Monoid[A].combine)

  trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  val list1 = List(1, 2, 3)
  val list3 = list1.as("AS")
  val list2 = Functor[List].map(list1)(_ * 2)

}
