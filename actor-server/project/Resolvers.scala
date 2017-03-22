package im.actor

import sbt._

object Resolvers {
  lazy val seq = Seq(
    DefaultMavenRepository,
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "Nexus DiegoSilva Releases" at "http://nexus.diegosilva.com.br:8081/nexus/content/repositories/releases/",
      "Nexus DiegoSilva Snapshots" at "http://nexus.diegosilva.com.br:8081/nexus/content/repositories/snapshots/",
    // for op-rabbit
    "The New Motion Public Repo" at "http://nexus.thenewmotion.com/content/groups/public/",
    // for akka-rabbitmq (needed by op-rabbit)
    "SpinGo OSS" at "http://spingo-oss.s3.amazonaws.com/repositories/releases"
  )
}
