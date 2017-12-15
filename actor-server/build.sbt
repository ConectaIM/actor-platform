import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm
import im.actor.{Configs, Dependencies, Resolvers, SbtActorApi, Versioning}
import sbt.Keys.{baseDirectory, libraryDependencies, unmanagedResourceDirectories}

val ScalaVersion = "2.11.11"
val BotKitVersion = Versioning.getVersion

resolvers ++= Seq(
  "Nexus DiegoSilva Releases" at "http://nexus.diegosilva.com.br:8081/content/repositories/releases/",
  "Nexus DiegoSilva Snapshots" at "http://nexus.diegosilva.com.br:8081/content/repositories/snapshots/"
)

lazy val buildSettings =
  Defaults.coreDefaultSettings ++
    Seq(
      //version := Version,
      scalaVersion := ScalaVersion,
      scalaVersion in ThisBuild := ScalaVersion,
      crossPaths := false,
      organization := "im.actor.server",
      organizationHomepage := Some(url("https://actor.im")),
      //resolvers ++= Resolvers.seq,
      //        scalacOptions ++= Seq(
      //          "-Ywarn-unused",
      //          "-Ywarn-adapted-args",
      //          "-Ywarn-nullary-override",
      //          "-Ywarn-nullary-unit",
      //          "-Ywarn-value-discard"
      //        ),
      parallelExecution := true,
      resolvers ++= Seq(
        "Nexus DiegoSilva Releases" at "http://nexus.diegosilva.com.br:8081/content/repositories/releases/",
        "Nexus DiegoSilva Snapshots" at "http://nexus.diegosilva.com.br:8081/content/repositories/snapshots/"
      )
    ) //++ im.actor.Sonatype.sonatypeSettings

lazy val pomExtraXml =
  <url>https://actor.im</url>
    <scm>
      <connection>scm:git:github.com/dfsilva/actor-platform.git</connection>
      <developerConnection>scm:git:git@github.com:dfsilva/actor-platform.git</developerConnection>
      <url>github.com/dfsilva</url>
    </scm>
    <developers>
      <developer>
        <id>prettynatty</id>
        <name>Andrey Kuznetsov</name>
        <url>https://github.com/prettynatty</url>
      </developer>
      <developer>
        <id>rockjam</id>
        <name>Nikolay Tatarinov</name>
        <url>https://github.com/rockjam</url>
      </developer>
      <developer>
        <id>dfsilva</id>
        <name>Diego Silva</name>
        <url>https://github.com/dfsilva</url>
      </developer>
    </developers>

lazy val defaultSettingsBotkit =
  buildSettings ++ Publishing.publishSettings(
    pomExtraXml,"im.actor.server") ++ Seq(resolvers ++= Resolvers.seq)

lazy val protobuffSettings = Seq(

//  PB.protoSources in Compile := Seq(
//    file("actor-models/src/main/protobuf"),
//    file("actor-core/src/main/protobuf")
//    file("actor-fs-adapters/src/main/protobuf")
//  ),

  PB.includePaths in Compile := Seq(
    file("actor-core/target/protobuf_external"),
    file("actor-models/src/main/protobuf"),
    file("actor-core/src/main/protobuf"),
    file("actor-fs-adapters/src/main/protobuf"),
    file("actor-session-messages/src/main/protobuf"),
    file("actor-bots/src/main/protobuf"),
    file("actor-notify/src/main/protobuf"),
    file("actor-search/src/main/protobuf")
    ),

    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value
    ),

  libraryDependencies ++= Dependencies.protocScalaPbRuntime,

  unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "main" / "protobuf"
)

lazy val defaultSettingsServer =
  buildSettings ++ Publishing.publishSettings(
    pomExtraXml,"im.actor.server") ++
    protobuffSettings ++
    Seq(initialize ~= { _ =>
      if (sys.props("java.specification.version") != "1.8")
        sys.error("Java 8 is required for this project.")
      },
      resolvers ++= Resolvers.seq,
      fork in Test := false,
      updateOptions := updateOptions.value.withCachedResolution(true),
      addCompilerPlugin("com.github.ghik" % "silencer-plugin" % "0.4"))


lazy val root = Project(
  "actor",
  file("."),
  settings =
//    Packaging.packagingSettings ++
//      defaultSettingsServer ++
//      Revolver.settings ++
      Seq(
        libraryDependencies ++= Dependencies.root,
        //Revolver.reStartArgs := Seq("im.actor.server.Main"),
        mainClass in Revolver.reStart := Some("im.actor.server.Main"),
        mainClass in Compile := Some("im.actor.server.Main"),
        autoCompilerPlugins := true,
        scalacOptions in(Compile, doc) ++= Seq(
          "-Ywarn-unused-import",
          "-groups",
          "-implicits",
          "-diagrams"
        )
      )
)
  //.settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
//  .settings(Releasing.releaseSettings)
  .dependsOn(actorServerSdk)
  .aggregate(
    actorServerSdk,
    actorTestkit,
    actorTests
  )
  .settings(aggregate in Revolver.reStart := false)
  .enablePlugins(JavaServerAppPackaging, JDebPackaging)

lazy val actorActivation = Project(
  id = "actor-activation",
  base = file("actor-activation"),
  settings = defaultSettingsServer ++
    Seq(
      libraryDependencies ++= Dependencies.activation,
      scalacOptions in Compile := (scalacOptions in Compile).value.filterNot(_ == "-Ywarn-unused-import")
    )
)
  .dependsOn(actorCore, actorEmail, actorSms, actorPersist)

lazy val actorBots = Project(
  id = "actor-bots",
  base = file("actor-bots"),
  settings = defaultSettingsServer
    ++ Seq(libraryDependencies ++= Dependencies.bots)
  )
  .dependsOn(actorCore, actorHttpApi, actorTestkit % "test")

lazy val actorBotsShared = Project(
  id = "actor-bots-shared",
  base = file("actor-bots-shared"),
  settings = defaultSettingsBotkit ++ Seq(
    version := BotKitVersion,
    libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _),
    libraryDependencies ++= Dependencies.botShared,
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  )
)

lazy val actorBotkit = Project(
  id = "actor-botkit",
  base = file("actor-botkit"),
  settings = defaultSettingsBotkit ++ Revolver.settings ++ Seq(
    version := BotKitVersion,
    libraryDependencies ++= Dependencies.botkit,
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  )
)
  .dependsOn(actorBotsShared)
  .aggregate(actorBotsShared)

lazy val actorCli = Project(
  id = "actor-cli",
  base = file("actor-cli"),
  settings = defaultSettingsServer ++ Revolver.settings ++ Seq(
    libraryDependencies ++= Dependencies.cli,
    mainClass in Revolver.reStart := Some("im.actor.server.cli.ActorCli"),
    mainClass in Compile := Some("im.actor.server.cli.ActorCli")
  )
)
  .dependsOn(actorCore, actorFrontend)

lazy val actorCore = Project(
  id = "actor-core",
  base = file("actor-core"),
  settings = defaultSettingsServer
    ++ SbtActorApi.settings
    ++ Seq(
      libraryDependencies ++= Dependencies.core
    )
)
  .dependsOn(actorCodecs, actorFileAdapter, actorModels, actorPersist, actorRuntime)

lazy val actorEmail = Project(
  id = "actor-email",
  base = file("actor-email"),
  settings = defaultSettingsServer ++
    Seq(
      libraryDependencies ++= Dependencies.email
    )
)
  .dependsOn(actorRuntime)

lazy val actorEnrich = Project(
  id = "actor-enrich",
  base = file("actor-enrich"),
  settings = defaultSettingsServer ++ Seq(
    libraryDependencies ++= Dependencies.enrich
  )
)
  .dependsOn(actorRpcApi, actorRuntime)

lazy val actorHttpApi = Project(
  id = "actor-http-api",
  base = file("actor-http-api"),
  settings = defaultSettingsServer ++ Seq(
    libraryDependencies ++= Dependencies.httpApi
  )
)
  .dependsOn(actorPersist, actorRuntime)//runtime deps because of ActorConfig

lazy val actorNotify = Project(
  id = "actor-notify",
  base = file("actor-notify"),
  settings = defaultSettingsServer
    ++ Seq(libraryDependencies ++= Dependencies.shared)
)
  .dependsOn(actorCore, actorEmail)

lazy val actorOAuth = Project(
  id = "actor-oauth",
  base = file("actor-oauth"),
  settings = defaultSettingsServer ++
    Seq(
      libraryDependencies ++= Dependencies.oauth
    )
)
  .dependsOn(actorPersist)

lazy val actorSearch = Project(
  id = "actor-search",
  base = file("actor-search"),
  settings = defaultSettingsServer ++ Seq(libraryDependencies ++= Dependencies.search)
).dependsOn(actorRpcApi)

lazy val actorSession = Project(
  id = "actor-session",
  base = file("actor-session"),
  settings = defaultSettingsServer ++ Seq(
    libraryDependencies ++= Dependencies.session
  )
)
  .dependsOn(actorCodecs, actorCore, actorPersist, actorRpcApi)

lazy val actorSessionMessages = Project(
  id = "actor-session-messages",
  base = file("actor-session-messages"),
  settings = defaultSettingsServer
    ++ Seq(libraryDependencies ++= Dependencies.sessionMessages)
  )
  .dependsOn(actorCore)

lazy val actorRpcApi = Project(
  id = "actor-rpc-api",
  base = file("actor-rpc-api"),
  settings = defaultSettingsServer ++ Seq(
    libraryDependencies ++= Dependencies.rpcApi
  )
)
  .dependsOn(
    actorActivation,
    actorCore,
    actorOAuth,
    actorSessionMessages,
    actorSms)

lazy val actorSms = Project(
  id = "actor-sms",
  base = file("actor-sms"),
  settings = defaultSettingsServer ++ Seq(libraryDependencies ++= Dependencies.sms)
)
  .dependsOn(actorRuntime)

lazy val actorFileAdapter = Project(
  id = "actor-fs-adapters",
  base = file("actor-fs-adapters"),
  settings = defaultSettingsServer
    ++ Seq(
    libraryDependencies ++= Dependencies.fileAdapter
  )
)
  .dependsOn(actorHttpApi, actorPersist)

lazy val actorFrontend = Project(
  id = "actor-frontend",
  base = file("actor-frontend"),
  settings = defaultSettingsServer ++ Seq(
    libraryDependencies ++= Dependencies.frontend
  )
)
  .dependsOn(actorCore, actorSession)

lazy val actorCodecs = Project(
  id = "actor-codecs",
  base = file("actor-codecs"),
  settings = defaultSettingsServer ++ Seq(
    libraryDependencies ++= Dependencies.codecs
  )
)
  .dependsOn(actorModels)

lazy val actorModels = Project(
  id = "actor-models",
  base = file("actor-models"),
  settings = defaultSettingsServer
    ++ Seq(
    libraryDependencies ++= Dependencies.models
  )
)

lazy val actorPersist = Project(
  id = "actor-persist",
  base = file("actor-persist"),
  settings = defaultSettingsServer ++ Seq(
    libraryDependencies ++= Dependencies.persist
  )
)
  .dependsOn(actorModels, actorRuntime)

lazy val actorTestkit = Project(
  id = "actor-testkit",
  base = file("actor-testkit"),
  settings = defaultSettingsServer ++ Seq(
    libraryDependencies ++= Dependencies.tests
  )
).configs(Configs.all: _*)
  .dependsOn(
    actorCore,
    actorRpcApi,
    actorSession
  )

lazy val actorRuntime = Project(
  id = "actor-runtime",
  base = file("actor-runtime"),
  settings = defaultSettingsServer ++ Seq(
    libraryDependencies ++= Dependencies.runtime
  )
)

lazy val actorServerSdk = Project(
  id = "actor-server-sdk",
  base = file("actor-server-sdk"),
  settings = defaultSettingsServer ++ Seq(
    libraryDependencies ++= Dependencies.sdk
  )
)
  .dependsOn(
    actorActivation,
    actorBots,
    actorCli,
    actorEnrich,
    actorEmail,
    actorFrontend,
    actorHttpApi,
    actorNotify,
    actorOAuth,
    actorRpcApi
  ).aggregate(
  actorActivation,
  actorBots,
  actorCli,
  actorCodecs,
  actorCore,
  actorEmail,
  actorEnrich,
  actorFileAdapter,
  actorFrontend,
  actorHttpApi,
  actorModels,
  actorNotify,
  actorOAuth,
  actorPersist,
  actorRpcApi,
  actorRuntime,
  actorSearch,
  actorSession,
  actorSessionMessages,
  actorSms
)

lazy val actorTests = Project(
  id = "actor-tests",
  base = file("actor-tests"),
  settings = defaultSettingsServer ++ /*Testing.settings ++ */Seq(
    libraryDependencies ++= Dependencies.tests,
    compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
    scalacOptions in Compile := (scalacOptions in Compile).value.filterNot(_ == "-Xfatal-warnings"),
    executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
      case (testResults, multiNodeResults)  =>
        val overall =
          if (testResults.overall.id < multiNodeResults.overall.id)
            multiNodeResults.overall
          else
            testResults.overall
        Tests.Output(overall,
          testResults.events ++ multiNodeResults.events,
          testResults.summaries ++ multiNodeResults.summaries)
    }
  ))
  .configs(Configs.all: _*)
  .configs(MultiJvm)
  .dependsOn(
    actorTestkit % "test",
    actorActivation,
    actorBots,
    actorCodecs,
    actorCore,
    actorEmail,
    actorEnrich,
    actorFrontend,
    actorHttpApi,
    actorOAuth,
    actorPersist,
    actorRpcApi,
    actorSession
  )


