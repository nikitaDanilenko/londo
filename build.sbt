name := "londo"
organization := "io.danilenko"
version := "0.1.0"

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayScala)

scalaVersion := "2.13.3"

val doobieVersion = "0.10.0"
val circeVersion = "0.13.0"
val jwtVersion = "7.1.2"
val quillVersion = "3.6.0-RC1"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play"     %% "scalatestplus-play"   % "5.0.0" % Test,
  "org.postgresql"              % "postgresql"           % "42.2.8",
  "io.getquill"                %% "quill-jdbc"           % quillVersion,
  "io.getquill"                %% "quill-codegen-jdbc"   % quillVersion,
  "io.getquill"                %% "quill-async-postgres" % quillVersion,
  "org.flywaydb"               %% "flyway-play"          % "6.0.0",
  "org.typelevel"              %% "cats-core"            % "2.2.0",
  "org.scalameta"              %% "scalafmt-dynamic"     % "2.6.1",
  "com.github.pathikrit"       %% "better-files"         % "3.9.1",
  "org.scalameta"              %% "scalameta"            % "4.3.13",
  "org.tpolecat"               %% "doobie-core"          % doobieVersion,
  "org.tpolecat"               %% "doobie-hikari"        % doobieVersion,
  "org.tpolecat"               %% "doobie-postgres"      % doobieVersion,
  "org.tpolecat"               %% "doobie-quill"         % doobieVersion,
  "io.circe"                   %% "circe-core"           % circeVersion,
  "io.circe"                   %% "circe-generic"        % circeVersion,
  "io.circe"                   %% "circe-parser"         % circeVersion,
  "org.sangria-graphql"        %% "sangria"              % "2.1.0",
  "org.sangria-graphql"        %% "sangria-circe"        % "1.3.1",
  "com.dripower"               %% "play-circe"           % "2812.0",
  "com.github.jwt-scala"       %% "jwt-core"             % jwtVersion,
  "com.github.jwt-scala"       %% "jwt-circe"            % jwtVersion,
  "org.typelevel"              %% "spire"                % "0.17.0",
  "com.beachape"               %% "enumeratum-circe"     % "1.6.1",
  "com.github.julien-truffaut" %% "monocle-core"         % "3.0.0-M5",
  "com.github.julien-truffaut" %% "monocle-macro"        % "3.0.0-M5"
)

scalacOptions ++= Seq(
  "-Ymacro-annotations"
)

lazy val dbGenerate = Command.command("dbGenerate") { state =>
  "runMain db.generators.CodeGenerator" :: state
}

commands += dbGenerate

lazy val daoGenerate = Command.command("daoGenerate") { state =>
  "runMain db.generators.DaoGenerator" :: state
}

commands += daoGenerate
