name := """londo"""
organization := "io.danilenko"

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "3.0.0-M3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies := libraryDependencies.value.map(_.withDottyCompat(scalaVersion.value))
