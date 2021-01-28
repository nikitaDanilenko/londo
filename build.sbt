name := "londo"
organization := "io.danilenko"
version := "0.1.0"

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play"   % "5.0.0" % Test,
  "org.postgresql"          % "postgresql"           % "42.2.8",
  "io.getquill"            %% "quill-jdbc"           % "3.6.0-RC1",
  "io.getquill"            %% "quill-codegen-jdbc"   % "3.6.0-RC1",
  "io.getquill"            %% "quill-async-postgres" % "3.6.0-RC1",
  "org.flywaydb"           %% "flyway-play"          % "6.0.0",
  "org.typelevel"          %% "cats-core"            % "2.2.0"
)

lazy val dbGenerate = Command.command("dbGenerate") { state =>
  "runMain db.CodeGenerator" :: state
}

commands += dbGenerate
