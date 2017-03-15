addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.6")
libraryDependencies += "com.trueaccord.scalapb" %% "compilerplugin" % "0.5.47" excludeAll(ExclusionRule(organization = "com.trueaccord.lenses"))