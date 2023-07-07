import sbt._

object Dependencies {

  object Versions {
    val cats = "2.7.0"
    val catsEffect = "3.5.1"
    val catsRetry = "3.1.0"
    val circe = "0.14.5"
    val derevo = "0.13.0"

    val http4s = "0.23.22"
    val log4cats = "2.3.1"
    val monocle = "3.2.0"
    val newtype = "0.4.4"
    val refined = "0.11.0"

    val betterMonadicFor = "0.3.1"
    val kindProjector = "0.13.2"
    val logback = "1.2.11"
    val organizeImports = "0.6.0"
    val semanticDB = "4.8.2"

    val munit = "1.0.7"
  }

  object Libraries {
    def circe(artifact: String): ModuleID =
      "io.circe" %% s"circe-$artifact" % Versions.circe
    def derevo(artifact: String): ModuleID =
      "tf.tofu" %% s"derevo-$artifact" % Versions.derevo
    def http4s(artifact: String): ModuleID =
      "org.http4s" %% s"http4s-$artifact" % Versions.http4s

    val cats = "org.typelevel" %% "cats-core" % Versions.cats
    val catsEffect = "org.typelevel" %% "cats-effect" % Versions.catsEffect
    val catsEffectKernel =
      "org.typelevel" %% "cats-effect-kernel" % Versions.catsEffect
    val catsEffectStd =
      "org.typelevel" %% "cats-effect-std" % Versions.catsEffect
    val catsRetry = "com.github.cb372" %% "cats-retry" % Versions.catsRetry

    val circeCore = circe("core")
    val circeGeneric = circe("generic")
    val circeParser = circe("parser")
    val circeRefined = circe("refined")

    val derevoCore = derevo("core")
    val derevoCats = derevo("cats")
    val derevoCirce = derevo("circe-magnolia")

    val http4sDsl = http4s("dsl")
    val http4sClient = http4s("ember-client")
    val http4sServer = http4s("ember-server")
    val http4sCirce = http4s("circe")

    val monocleCore = "dev.optics" %% "monocle-core" % Versions.monocle

    val refinedCore = "eu.timepit" %% "refined" % Versions.refined
    val refinedCats = "eu.timepit" %% "refined-cats" % Versions.refined

    val log4cats = "org.typelevel" %% "log4cats-slf4j" % Versions.log4cats
    val newtype = "io.estatico" %% "newtype" % Versions.newtype

    // Runtime
    val logback = "ch.qos.logback" % "logback-classic" % Versions.logback

    // Test
    val catsLaws = "org.typelevel" %% "cats-laws" % Versions.cats
    val log4catsNoOp = "org.typelevel" %% "log4cats-noop" % Versions.log4cats
    val monocleLaw = "dev.optics" %% "monocle-law" % Versions.monocle
    val refinedScalacheck =
      "eu.timepit" %% "refined-scalacheck" % Versions.refined

    val munitCE = "org.typelevel" %% "munit-cats-effect-3" % Versions.munit
    val munitScalacheck = "org.scalameta" %% "munit-scalacheck" % Versions.munit

    // Scalafix rules
    val organizeImports =
      "com.github.liancheng" %% "organize-imports" % Versions.organizeImports
  }

  object CompilerPlugin {
    val betterMonadicFor = compilerPlugin(
      "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor
    )
    val kindProjector = compilerPlugin(
      "org.typelevel" % "kind-projector" % Versions.kindProjector cross CrossVersion.full
    )
    val semanticDB = compilerPlugin(
      "org.scalameta" % "semanticdb-scalac" % Versions.semanticDB cross CrossVersion.full
    )
  }

}
