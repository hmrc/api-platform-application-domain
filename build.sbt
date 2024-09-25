import scoverage.ScoverageKeys
import sbt._
import sbt.Keys._
import uk.gov.hmrc.DefaultBuildSettings.targetJvm
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion
import bloop.integrations.sbt.BloopDefaults
  
Global / bloopAggregateSourceDependencies := true

val appName = "api-platform-application-domain"

lazy val scala2_13 = "2.13.12"

ThisBuild / majorVersion     := 0
ThisBuild / isPublicArtefact := true
ThisBuild / scalaVersion     := scala2_13

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val library = (project in file("."))
  .settings(
    publish / skip := true
  )
  .aggregate(
    apiPlatformApplicationDomain, apiPlatformTestApplicationDomain
  )


lazy val apiPlatformApplicationDomain = Project("api-platform-application-domain", file("api-platform-application-domain"))
  .settings(
    libraryDependencies ++= LibraryDependencies.applicationDomain,
    ScoverageSettings(),
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-eT"),

    // Compile / unmanagedSourceDirectories += baseDirectory.value / ".." / "common" / "src" / "main" / "scala",
    // Test / unmanagedSourceDirectories += baseDirectory.value / ".." / "common" / "src" / "test" / "scala",
    
    Test / unmanagedSourceDirectories += baseDirectory.value / ".." / "test-application" / "src" / "main" / "scala"
  )
  .disablePlugins(JUnitXmlReportPlugin)


lazy val apiPlatformTestApplicationDomain = Project("api-platform-test-application-domain", file("api-platform-test-application-domain"))
  .dependsOn(
    apiPlatformApplicationDomain
  )
  .settings(
    libraryDependencies ++= LibraryDependencies.root,
    ScoverageKeys.coverageEnabled := false,
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-eT"),

    // Compile / unmanagedSourceDirectories += baseDirectory.value / ".." / "common" / "src" / "main" / "scala",

    Compile / unmanagedSourceDirectories += baseDirectory.value / ".." / "test-application" / "src" / "main" / "scala"
  )
  .disablePlugins(JUnitXmlReportPlugin)


  commands ++= Seq(
    Command.command("run-all-tests") { state => "test" :: state },
    Command.command("clean-and-test") { state => "clean" :: "compile" :: "run-all-tests" :: state },
    Command.command("pre-commit") { state => "clean" :: "scalafmtAll" :: "scalafixAll" ::"coverage" :: "run-all-tests" :: "coverageOff" :: "coverageAggregate" :: state }
  )
