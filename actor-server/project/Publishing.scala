import sbt.Keys._
import sbt._

import scala.xml.NodeSeq

object Publishing {

  def publishSettings(pomExtraVal: NodeSeq, org: String): Seq[Def.Setting[_]] = {
    Seq(
      publishTo := {
        val nexus = "http://nexus.diegosilva.com.br:8081/nexus/"
        if (isSnapshot.value)
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "content/repositories/releases")
      },
      credentials += Credentials("Sonatype Nexus Repository Manager", "nexus.diegosilva.com.br", "admin", "admin123"),
      organization := org,
      licenses := Seq("Apache v2" -> url("https://github.com/actorapp/actor-platform/blob/master/LICENSE")),
      pomExtra in Global := pomExtraVal
    ) ++ actorCompileSettings
  }

  private lazy val actorCompileSettings = Seq(
    scalacOptions in Compile ++= Seq(
      "-target:jvm-1.8",
      "-Ybackend:GenBCode",
      "-Ydelambdafy:method",
      "-Yopt:l:classpath",
      "-encoding", "UTF-8",
      "-deprecation",
      "-unchecked",
      "-feature",
      "-language:higherKinds",
      "-Xfatal-warnings",
      "-Xlint",
      "-Xfuture",
      "-Ywarn-dead-code",
      "-Ywarn-infer-any",
      "-Ywarn-numeric-widen"
    ),
    javaOptions ++= Seq("-Dfile.encoding=UTF-8"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation")
  )

}
