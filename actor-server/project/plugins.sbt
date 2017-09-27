resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Nexus DiegoSilva Releases" at "http://nexus.diegosilva.com.br:8081/content/repositories/releases/",
  "Nexus DiegoSilva Snapshots" at "http://nexus.diegosilva.com.br:8081/content/repositories/snapshots/",
//  Resolver.url("actor-sbt-plugins", url("https://dl.bintray.com/actor/sbt-plugins"))(Resolver.ivyStylePatterns),
  "Flyway" at "http://flywaydb.org/repo",
  "Era7 maven releases" at "https://s3-eu-west-1.amazonaws.com/releases.era7.com",
  Classpaths.sbtPluginReleases
)

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")

addSbtPlugin("im.actor" %% "sbt-actor-api" % "0.7.34-SNAPSHOT")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

//addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.5.1")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.6")

addSbtPlugin("ohnosequences" % "sbt-github-release" % "0.3.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-aspectj" % "0.10.0")

//addSbtPlugin("im.actor" % "actor-sbt-houserules" % "0.1.11-SNAPSHOT")

addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.3.8")
