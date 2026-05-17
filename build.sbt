import com.typesafe.config.ConfigFactory

name         := "londo"
organization := "io.danilenko"
version      := "0.1.0"

val circeVersion = "0.14.6"
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
    scalaVersion := "2.13.12",
    libraryDependencies ++= Seq(
      guice,
      "org.postgresql"              % "postgresql"           % "42.7.0",
      "org.flywaydb"               %% "flyway-play"          % "9.0.0",
      "com.typesafe.slick"         %% "slick"                % slickVersion,
      "com.typesafe.slick"         %% "slick-hikaricp"       % slickVersion,
      "com.typesafe.slick"         %% "slick-codegen"        % slickVersion,
      "org.typelevel"              %% "cats-core"            % "2.10.0",
      "org.typelevel"              %% "cats-effect"          % "3.4.9",
      "org.scalameta"              %% "scalafmt-dynamic"     % "3.7.2",
      "ch.qos.logback"              % "logback-classic"      % "1.4.7",
      "io.circe"                   %% "circe-core"           % circeVersion,
      "io.circe"                   %% "circe-generic"        % circeVersion,
      "io.circe"                   %% "circe-parser"         % circeVersion,
      "org.sangria-graphql"        %% "sangria"              % "3.5.3",
      "org.sangria-graphql"        %% "sangria-circe"        % "1.3.2",
      "org.playframework"          %% "play-slick"           % "6.0.0",
      "com.dripower"               %% "play-circe"           % "3014.1",
      "com.github.jwt-scala"       %% "jwt-core"             % jwtVersion,
      "com.github.jwt-scala"       %% "jwt-circe"            % jwtVersion,
      "org.typelevel"              %% "spire"                % "0.18.0",
      "com.beachape"               %% "enumeratum-circe"     % "1.7.3",
      "com.github.julien-truffaut" %% "monocle-core"         % "3.0.0-M5",
      "com.github.julien-truffaut" %% "monocle-macro"        % "3.0.0-M5",
      "io.circe"                   %% "circe-generic-extras" % "0.14.3",
      "com.typesafe"                % "config"               % "1.4.2",
      "io.scalaland"               %% "chimney"              % "0.7.2",
      "com.github.pureconfig"      %% "pureconfig"           % "0.17.4",
      "org.playframework"          %% "play-mailer"          % "10.0.0",
      "org.playframework"          %% "play-mailer-guice"    % "10.0.0",
      "com.kubukoz"                %% "slick-effect"         % "0.5.0",
      "com.kubukoz"                %% "slick-effect-catsio"  % "0.5.0",
      "com.lihaoyi"                %% "pprint"               % "0.8.1",
      // Transitive dependency. Override added for proper version.
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.16.0"
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

