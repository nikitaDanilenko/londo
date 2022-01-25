name := "londo"
organization := "io.danilenko"
version := "0.1.0"

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayScala)

scalaVersion := "2.13.3"

val doobieVersion = "0.13.4"
val circeVersion = "0.14.1"
val jwtVersion = "9.0.2"
val quillVersion = "3.6.0-RC1"

libraryDependencies ++= Seq(
  guice,
  "org.postgresql"              % "postgresql"           % "42.3.1",
  "io.getquill"                %% "quill-jdbc"           % quillVersion,
  "io.getquill"                %% "quill-codegen-jdbc"   % quillVersion,
  "io.getquill"                %% "quill-async-postgres" % quillVersion,
  "org.flywaydb"               %% "flyway-play"          % "7.18.0",
  "org.typelevel"              %% "cats-core"            % "2.7.0",
  "org.scalameta"              %% "scalafmt-dynamic"     % "3.3.0",
  "com.github.pathikrit"       %% "better-files"         % "3.9.1",
  "org.scalameta"              %% "scalameta"            % "4.4.32",
  "org.tpolecat"               %% "doobie-core"          % doobieVersion,
  "org.tpolecat"               %% "doobie-hikari"        % doobieVersion,
  "org.tpolecat"               %% "doobie-postgres"      % doobieVersion,
  "org.tpolecat"               %% "doobie-quill"         % doobieVersion,
  "io.circe"                   %% "circe-core"           % circeVersion,
  "io.circe"                   %% "circe-generic"        % circeVersion,
  "io.circe"                   %% "circe-parser"         % circeVersion,
  "org.sangria-graphql"        %% "sangria"              % "2.1.5",
  "org.sangria-graphql"        %% "sangria-circe"        % "1.3.2",
  "com.dripower"               %% "play-circe"           % "2814.2",
  "com.github.jwt-scala"       %% "jwt-core"             % jwtVersion,
  "com.github.jwt-scala"       %% "jwt-circe"            % jwtVersion,
  "org.typelevel"              %% "spire"                % "0.17.0",
  "com.beachape"               %% "enumeratum-circe"     % "1.7.0",
  "com.github.julien-truffaut" %% "monocle-core"         % "3.0.0-M5",
  "com.github.julien-truffaut" %% "monocle-macro"        % "3.0.0-M5",
  "io.circe"                   %% "circe-generic-extras" % circeVersion,
  "com.davegurnell"            %% "bridges"              % "0.24.0"
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

lazy val elmGenerate = Command.command("elmGenerate") { state =>
  "runMain elm.BridgeGenerator" :: state
}

commands += elmGenerate
