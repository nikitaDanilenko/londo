import com.typesafe.config.ConfigFactory

name         := "londo"
organization := "io.danilenko"
version      := "0.1.0"

val config = ConfigFactory
  .parseFile(new File("conf/application.conf"))
  .resolve()

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(CodegenPlugin)
  .enablePlugins(JavaServerAppPackaging)
  .settings(
    scalaVersion := "2.13.18",
    libraryDependencies ++= Seq(
      guice,
      "org.postgresql"              % "postgresql"           % DependencyVersions.Postgresql,
      "org.flywaydb"               %% "flyway-play"          % DependencyVersions.FlywayPlay,
      "com.typesafe.slick"         %% "slick"                % DependencyVersions.Slick,
      "com.typesafe.slick"         %% "slick-hikaricp"       % DependencyVersions.Slick,
      "com.typesafe.slick"         %% "slick-codegen"        % DependencyVersions.Slick,
      "org.typelevel"              %% "cats-core"            % DependencyVersions.CatsCore,
      "org.typelevel"              %% "cats-effect"          % DependencyVersions.CatsEffect,
      "org.scalameta"              %% "scalafmt-dynamic"     % DependencyVersions.ScalafmtDynamic,
      "ch.qos.logback"              % "logback-classic"      % DependencyVersions.LogbackClassic,
      "io.circe"                   %% "circe-core"           % DependencyVersions.Circe,
      "io.circe"                   %% "circe-generic"        % DependencyVersions.Circe,
      "io.circe"                   %% "circe-parser"         % DependencyVersions.Circe,
      "org.sangria-graphql"        %% "sangria"              % DependencyVersions.Sangria,
      "org.sangria-graphql"        %% "sangria-circe"        % DependencyVersions.SangriaCirce,
      "org.playframework"          %% "play-slick"           % DependencyVersions.PlaySlick,
      "com.dripower"               %% "play-circe"           % DependencyVersions.PlayCirce,
      "com.github.jwt-scala"       %% "jwt-core"             % DependencyVersions.Jwt,
      "com.github.jwt-scala"       %% "jwt-circe"            % DependencyVersions.Jwt,
      "org.typelevel"              %% "spire"                % DependencyVersions.Spire,
      "com.beachape"               %% "enumeratum-circe"     % DependencyVersions.EnumeratumCirce,
      "com.github.julien-truffaut" %% "monocle-core"         % DependencyVersions.Monocle,
      "com.github.julien-truffaut" %% "monocle-macro"        % DependencyVersions.Monocle,
      "io.circe"                   %% "circe-generic-extras" % DependencyVersions.CirceGenericExtras,
      "com.typesafe"                % "config"               % DependencyVersions.Config,
      "io.scalaland"               %% "chimney"              % DependencyVersions.Chimney,
      "com.github.pureconfig"      %% "pureconfig"           % DependencyVersions.Pureconfig,
      "org.playframework"          %% "play-mailer"          % DependencyVersions.PlayMailer,
      "org.playframework"          %% "play-mailer-guice"    % DependencyVersions.PlayMailer,
      "com.kubukoz"                %% "slick-effect"         % DependencyVersions.SlickEffect,
      "com.kubukoz"                %% "slick-effect-catsio"  % DependencyVersions.SlickEffect,
      "com.lihaoyi"                %% "pprint"               % DependencyVersions.Pprint,
      // Transitive dependency. Override added for proper version.
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % DependencyVersions.JacksonModuleScala
    ),
    dependencyOverrides ++= Seq(
      "com.google.inject" % "guice" % DependencyVersions.Guice
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
