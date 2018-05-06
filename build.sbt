name := "snowplow-intern"

version := "0.1"

scalaVersion := "2.12.6"

val http4sVersion = "0.18.10"
val circeVersion = "0.9.3"
val doobieVersion = "0.5.2"

scalacOptions ++= Seq(
  "-Ypartial-unification",
  "-unchecked",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",

  "-Xfatal-warnings",
)

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion,

  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.xerial" % "sqlite-jdbc" % "3.21.0.1",

  "com.github.java-json-tools" % "json-schema-validator" % "2.2.8",

  "com.lihaoyi" %% "utest" % "0.6.3" % "test"
)

testFrameworks += new TestFramework("utest.runner.Framework")

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)


cancelable in Global := true // enables Ctrl+C without quitting sbt
