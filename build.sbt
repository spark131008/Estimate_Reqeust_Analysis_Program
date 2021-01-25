scalaVersion := "2.13.3"

name := "Estimate Request Analysis Program"
organization := "com.tintmasteratl"
version := "1.0"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.1.1"
libraryDependencies += "au.com.bytecode" % "opencsv" % "2.4"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"