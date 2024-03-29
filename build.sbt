Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val cassandra = (project in file("modules/cassandra"))
  .settings(
    name                                   := "cassandra",
    version                                := "0.1",
    libraryDependencies += "co.fs2"        %% "fs2-io" % "3.9.2",
    libraryDependencies += "org.scalameta" %% "munit"  % "0.7.29" % Test
  )
  .dependsOn(macros)

lazy val macros = project
  .in(file("modules/macros"))
  .settings(
    name                                   := "macros",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
  )

lazy val root = project
  .in(file("."))
  .aggregate(cassandra, macros)
