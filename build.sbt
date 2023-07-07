import Dependencies.*
import sbt.*

ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.13.11"

// ScalaFix
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := "4.8.2"
ThisBuild / evictionErrorLevel := Level.Warn
ThisBuild / scalafixDependencies += Libraries.organizeImports

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

// Reload Sbt on changes to sbt or dependencies
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val testSettings: Seq[Def.Setting[_]] = List(
  Test / parallelExecution := false,
  publish / skip := true,
  fork := true
)

lazy val root = (project in file("."))
  .settings(
    Defaults.itSettings,
    IntegrationTest / parallelExecution := false,
    testFrameworks += new TestFramework("munit.Framework"),
    libraryDependencies += "org.typelevel" %% "munit-cats-effect-3" % Versions.munit % "it,test"
  )
  .settings(
    name := "cats-effect-3-quick-start",
    scalacOptions ++= List(
      "-Ymacro-annotations",
      "-Yrangepos",
      "-Wconf:cat=unused:info"
    ),
    libraryDependencies ++= Seq(
      // "core" module - IO, IOApp, schedulers
      // This pulls in the kernel and std modules automatically.
      Libraries.catsEffect,
      // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
      Libraries.catsEffectKernel,
      // standard "effect" library (Queues, Console, Random etc.)
      Libraries.catsEffectStd,
      // better monadic for compiler plugin as suggested by documentation

      Libraries.catsRetry,
      Libraries.circeCore,
      Libraries.circeGeneric,
      Libraries.circeParser,
      Libraries.circeRefined,
      Libraries.derevoCore,
      Libraries.derevoCats,
      Libraries.derevoCirce,
      Libraries.http4sCirce,
      Libraries.http4sClient,
      Libraries.http4sServer,
      Libraries.http4sDsl,
      Libraries.monocleCore,
      Libraries.newtype,
      Libraries.refinedCore,
      Libraries.refinedCats,
      CompilerPlugin.kindProjector,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.semanticDB
    )
  )

// CI build
addCommandAlias("buildShoppingCart", ";clean;+test;")
// ScalaFormat + ScalaFix
addCommandAlias("format", ";scalafixAll ;scalafmtAll ;scalafmtSbt")
