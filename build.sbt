name := "londo"
organization := "io.danilenko"
version := "0.1.0"

val dottyVersion = "3.0.0-M3"

scalaVersion := dottyVersion

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
)

libraryDependencies := libraryDependencies.value.map(_.withDottyCompat(scalaVersion.value))
