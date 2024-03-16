
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .settings(
    name := "cassandra",
    version := "0.1",
    scalaVersion := "3.4.0",
    scalacOptions ++= Seq("-explain", "-deprecation",  "-source:future", "-rewrite" , "-Ykind-projector"),
    libraryDependencies += "co.fs2" %% "fs2-io" % "3.9.2",
    libraryDependencies += "org.typelevel" %% "cats-laws" % "2.10.0",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
  )