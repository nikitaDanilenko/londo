import sbt._

object Dependencies {

  // Renovate's sbt manager can resolve variables, but not from a nested object.
  // For this reason, we limit the sharing to only those versions that are used multiple times.
  // All singleton occurrences are inlined directly.
  private val CirceVersion = "0.14.15"

  private val SlickVersion = "3.6.1"

  private val JwtVersion = "11.0.4"

  private val MonocleVersion = "3.0.0-M6"

  private val PlayMailerVersion = "10.0.1"

  private val SlickEffectVersion = "0.6.1"

  private val GuiceVersion = "6.0.0"

  // Dependencies

  val Postgresql = "org.postgresql" % "postgresql" % "42.7.11"

  val FlywayPlay = "org.flywaydb" %% "flyway-play" % "9.1.0"

  val Slick = "com.typesafe.slick" %% "slick" % SlickVersion

  val SlickHikaricp = "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion

  val SlickCodegen = "com.typesafe.slick" %% "slick-codegen" % SlickVersion

  val CatsCore = "org.typelevel" %% "cats-core" % "2.13.0"

  val CatsEffect = "org.typelevel" %% "cats-effect" % "3.4.11"

  val ScalafmtDynamic = "org.scalameta" %% "scalafmt-dynamic" % "3.7.17"

  val LogbackClassic = "ch.qos.logback" % "logback-classic" % "1.4.14"

  val CirceCore = "io.circe" %% "circe-core" % CirceVersion

  val CirceGeneric = "io.circe" %% "circe-generic" % CirceVersion

  val CirceParser = "io.circe" %% "circe-parser" % CirceVersion

  val Sangria = "org.sangria-graphql" %% "sangria" % "4.2.18"

  val SangriaCirce = "org.sangria-graphql" %% "sangria-circe" % "1.3.2"

  val PlaySlick = "org.playframework" %% "play-slick" % "6.2.0"

  val PlayCirce = "com.dripower" %% "play-circe" % "3014.1"

  val JwtCore = "com.github.jwt-scala" %% "jwt-core" % JwtVersion

  val JwtCirce = "com.github.jwt-scala" %% "jwt-circe" % JwtVersion

  val Spire = "org.typelevel" %% "spire" % "0.18.0"

  val EnumeratumCirce = "com.beachape" %% "enumeratum-circe" % "1.7.5"

  val MonocleCore = "com.github.julien-truffaut" %% "monocle-core" % MonocleVersion

  val MonocleMacro = "com.github.julien-truffaut" %% "monocle-macro" % MonocleVersion

  val CirceGenericExtras = "io.circe" %% "circe-generic-extras" % "0.14.4"

  val Config = "com.typesafe" % "config" % "1.4.8"

  val Chimney = "io.scalaland" %% "chimney" % "0.7.5"

  val Pureconfig = "com.github.pureconfig" %% "pureconfig" % "0.17.10"

  val PlayMailer = "org.playframework" %% "play-mailer" % PlayMailerVersion

  val PlayMailerGuice = "org.playframework" %% "play-mailer-guice" % PlayMailerVersion

  val SlickEffect = "com.kubukoz" %% "slick-effect" % SlickEffectVersion

  val SlickEffectCatsio = "com.kubukoz" %% "slick-effect-catsio" % SlickEffectVersion

  val Pprint = "com.lihaoyi" %% "pprint" % "0.9.6"

  // Transitive dependency. Override added for proper version.
  val JacksonModuleScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.16.2"

  // Dependency overrides

  val Guice = "com.google.inject" % "guice" % GuiceVersion

  val GuiceAssistedInject = "com.google.inject.extensions" % "guice-assistedinject" % GuiceVersion

  val all: Seq[ModuleID] = Seq(
    Postgresql,
    FlywayPlay,
    Slick,
    SlickHikaricp,
    SlickCodegen,
    CatsCore,
    CatsEffect,
    ScalafmtDynamic,
    LogbackClassic,
    CirceCore,
    CirceGeneric,
    CirceParser,
    Sangria,
    SangriaCirce,
    PlaySlick,
    PlayCirce,
    JwtCore,
    JwtCirce,
    Spire,
    EnumeratumCirce,
    MonocleCore,
    MonocleMacro,
    CirceGenericExtras,
    Config,
    Chimney,
    Pureconfig,
    PlayMailer,
    PlayMailerGuice,
    SlickEffect,
    SlickEffectCatsio,
    Pprint,
    JacksonModuleScala
  )

  val overrides: Seq[ModuleID] = Seq(
    Guice,
    GuiceAssistedInject
  )

}
