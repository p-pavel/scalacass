
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .settings(
    name := "cassandra",
    version := "0.1",
    scalaVersion := "3.3.3",
    scalacOptions ++= Seq("-explain", "-deprecation",  "-source:future", "-rewrite" , "-Ykind-projector"),
    libraryDependencies += "co.fs2" %% "fs2-io" % "3.9.2",
    libraryDependencies += "org.typelevel" %% "cats-laws" % "2.10.0",
  )