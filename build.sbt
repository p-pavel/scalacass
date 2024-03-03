
Global / semanticdbEnabled := true
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .settings(
    name := "cassandra",
    version := "0.1",
    scalaVersion := "3.3.3",
  )