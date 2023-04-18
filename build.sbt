import com.typesafe.config.ConfigFactory

name         := "londo"
organization := "io.danilenko"
version      := "0.1.0"

val circeVersion = "0.14.5"
val jwtVersion   = "9.2.0"
val slickVersion = "3.4.1"

val config = ConfigFactory
  .parseFile(new File("conf/application.conf"))
  .resolve()

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(CodegenPlugin)
  .enablePlugins(JavaServerAppPackaging)
  .settings(
    scalaVersion := "2.13.10",
    libraryDependencies ++= Seq(
      guice,
      "org.postgresql"              % "postgresql"           % "42.6.0",
      "org.flywaydb"               %% "flyway-play"          % "7.37.0",
      "com.typesafe.slick"         %% "slick"                % slickVersion,
      "com.typesafe.slick"         %% "slick-hikaricp"       % slickVersion,
      "com.typesafe.slick"         %% "slick-codegen"        % slickVersion,
      "org.typelevel"              %% "cats-core"            % "2.9.0",
      "org.scalameta"              %% "scalafmt-dynamic"     % "3.7.2",
      "com.github.pathikrit"       %% "better-files"         % "3.9.2",
      "org.scalameta"              %% "scalameta"            % "4.7.6",
      "ch.qos.logback"              % "logback-classic"      % "1.4.6",
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
    ),
    dependencyOverrides ++= Seq(
      "com.google.inject" % "guice" % "5.1.0"
    ),
    slickCodegenDatabaseUrl      := config.getString("slick.dbs.default.db.url"),
    slickCodegenDatabaseUser     := config.getString("slick.dbs.default.db.user"),
    slickCodegenDatabasePassword := config.getString("slick.dbs.default.db.password"),
    slickCodegenDriver           := slick.jdbc.PostgresProfile,
    slickCodegenJdbcDriver       := "org.postgresql.Driver",
    slickCodegenOutputPackage    := "db.generated",
    slickCodegenExcludedTables   := Seq("flyway_schema_history"),
    slickCodegenOutputDir        := baseDirectory.value / "app"
  )

scalacOptions ++= Seq(
  "-Ymacro-annotations"
)

Docker / maintainer    := "nikita.danilenko.is@gmail.com"
Docker / packageName   := "londo"
Docker / version       := sys.env.getOrElse("BUILD_NUMBER", "0")
Docker / daemonUserUid := None
Docker / daemonUser    := "daemon"
dockerBaseImage        := "adoptopenjdk/openjdk11:latest"
dockerUpdateLatest     := true

// Patches and workarounds

// Docker has known issues with Play's PID file. The below command disables Play's PID file.
// cf. https://www.playframework.com/documentation/2.8.x/Deploying#Play-PID-Configuration
// The setting is a possible duplicate of the same setting in the application.conf.
Universal / javaOptions ++= Seq(
  "-Dpidfile.path=/dev/null"
)
