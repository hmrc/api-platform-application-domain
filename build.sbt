import sbt._
import sbt.Keys._
import uk.gov.hmrc.DefaultBuildSettings.targetJvm
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion
import bloop.integrations.sbt.BloopDefaults

val appName = "api-platform-application-domain"
lazy val scala213 = "2.13.8"

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val library = Project(appName, file("."))
  .settings(
    scalaVersion                     := scala213,
    name                             := appName,
    majorVersion                     := 0,
    isPublicArtefact                 := true,
    libraryDependencies ++= LibraryDependencies()
  )
  .settings(
    ScoverageSettings()
  )
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-eT")
  )

  commands ++= Seq(
    Command.command("run-all-tests") { state => "test" :: state },

    Command.command("clean-and-test") { state => "clean" :: "compile" :: "run-all-tests" :: state },

    // Coverage does not need compile !
    Command.command("pre-commit") { state => "clean" :: "scalafmtAll" :: "scalafixAll" ::"coverage" :: "run-all-tests" :: "coverageReport" :: "coverageOff" :: state }
  )