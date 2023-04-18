name := "londo"
organization := "io.danilenko"
version := "0.1.0"

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(JavaServerAppPackaging)

scalaVersion := "2.13.10"

val doobieVersion = "0.13.4"
val circeVersion = "0.14.5"
val jwtVersion = "9.2.0"
val quillVersion = "3.6.0-RC1"

libraryDependencies ++= Seq(
  guice,
  "org.postgresql"              % "postgresql"           % "42.5.4",
  "io.getquill"                %% "quill-jdbc"           % quillVersion,
  "io.getquill"                %% "quill-codegen-jdbc"   % quillVersion,
  "io.getquill"                %% "quill-async-postgres" % quillVersion,
  "org.flywaydb"               %% "flyway-play"          % "7.37.0",
  "org.typelevel"              %% "cats-core"            % "2.9.0",
  "org.scalameta"              %% "scalafmt-dynamic"     % "3.7.2",
  "com.github.pathikrit"       %% "better-files"         % "3.9.2",
  "org.scalameta"              %% "scalameta"            % "4.7.6",
  "org.tpolecat"               %% "doobie-core"          % doobieVersion,
  "org.tpolecat"               %% "doobie-hikari"        % doobieVersion,
  "org.tpolecat"               %% "doobie-postgres"      % doobieVersion,
  "org.tpolecat"               %% "doobie-quill"         % doobieVersion,
  "io.circe"                   %% "circe-core"           % circeVersion,
  "io.circe"                   %% "circe-generic"        % circeVersion,
  "io.circe"                   %% "circe-parser"         % circeVersion,
  "org.sangria-graphql"        %% "sangria"              % "3.5.3",
  "org.sangria-graphql"        %% "sangria-circe"        % "1.3.2",
  "com.dripower"               %% "play-circe"           % "2814.2",
  "com.github.jwt-scala"       %% "jwt-core"             % jwtVersion,
  "com.github.jwt-scala"       %% "jwt-circe"            % jwtVersion,
  "org.typelevel"              %% "spire"                % "0.18.0",
  "com.beachape"               %% "enumeratum-circe"     % "1.7.2",
  "com.github.julien-truffaut" %% "monocle-core"         % "3.0.0-M5",
  "com.github.julien-truffaut" %% "monocle-macro"        % "3.0.0-M5",
  "io.circe"                   %% "circe-generic-extras" % "0.14.3",
  // Transitive dependency. Override added for proper version.
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.2"
)

dependencyOverrides ++= Seq(
  "com.google.inject" % "guice" % "5.1.0"
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

Docker / maintainer := "nikita.danilenko.is@gmail.com"
Docker / packageName := "londo"
Docker / version := sys.env.getOrElse("BUILD_NUMBER", "0")
Docker / daemonUserUid := None
Docker / daemonUser := "daemon"
dockerBaseImage := "adoptopenjdk/openjdk11:latest"
dockerUpdateLatest := true

// Patches and workarounds

// Docker has known issues with Play's PID file. The below command disables Play's PID file.
// cf. https://www.playframework.com/documentation/2.8.x/Deploying#Play-PID-Configuration
// The setting is a possible duplicate of the same setting in the application.conf.
Universal / javaOptions ++= Seq(
  "-Dpidfile.path=/dev/null"
)
