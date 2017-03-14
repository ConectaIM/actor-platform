addCommandAlias("debianPackage", "debian:packageBin")
addCommandAlias("debianPackageSystemd",
  "; set serverLoading in Debian := com.typesafe.sbt.packager.archetypes.ServerLoader.Systemd ;debian:packageBin"
)

defaultLinuxInstallLocation in Docker := "/var/lib/actor"

PB.protoSources in Compile := Seq(
  file("actor-models/src/main/protobuf"),
  file("actor-core/src/main/resources/protobuf"),
  file("actor-fs-adapters/src/main/protobuf"))
PB.includePaths in Compile := Seq(
  file("actor-models/src/main/protobuf"),
  file("actor-core/src/main/resources/protobuf"),
  file("actor-fs-adapters/src/main/protobuf"))
PB.targets in Compile := Seq(scalapb.gen() -> (sourceManaged in Compile).value)
