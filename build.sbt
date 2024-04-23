Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val cassandra = (project in file("modules/cassandra"))
  .enablePlugins(SbtOsgi)
  .settings(
    name                                   := "cassandra.protocol",
    version                                := "0.1",
    osgiSettings,
    OsgiKeys.exportPackage                 := Seq(
      "com.perikov.cassandra.protocol",
      "com.perikov.cassandra.protocol.grammar"
    ),
    OsgiKeys.importPackage                 := Seq(
      "*"
    ),
    OsgiKeys.requireCapability             :=
      "osgi.ee;filter:=\"(&(osgi.ee=JavaSE)(version=11))\"",
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
