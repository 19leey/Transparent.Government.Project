name := "TGP.Ingestion.Module"

version := "0.1"

scalaVersion := "2.13.3"

ThisBuild / useCoursier := false

val akkaVersion = "2.6.10"
val akkaHttpVersion = "10.2.1"
val playVersion = "2.9.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.play" %% "play-json" % playVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-mongodb" % "2.0.2",
  "org.mongodb" % "mongodb-driver-reactivestreams" % "1.12.0"
)