name := "akka-typed-from-scratch"

version := "0.1"

scalaVersion := "2.13.6"

val AkkaVersion = "2.6.15"
val AkkaHttpVersion = "10.2.6"

val AkkaStack = Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
)

ThisBuild / libraryDependencies ++= AkkaStack

lazy val root = Project(id = "root", base = file("."))
  .enablePlugins(JavaAppPackaging)