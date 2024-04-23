import sbt._
import sbt.Keys._

object MyPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override lazy val projectSettings   = Seq(
    scalaVersion := "3.4.1",
    organization := "com.perikov",
    scalacOptions ++= Seq(
      "-explain",
      "-deprecation",
      "-source:future",
      "-rewrite",
      "-Ykind-projector",
      " -java-output-version",
      "11"
    )
  )
}
