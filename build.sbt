name := "snowplow-intern"

version := "0.1"

scalaVersion := "2.12.6"

val http4sVersion = "0.18.10"
val circeVersion = "0.9.3"

scalacOptions ++= Seq(
  "-Ypartial-unification",
  "-unchecked",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  //"-Xfatal-warnings",
)

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)