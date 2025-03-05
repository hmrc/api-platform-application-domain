import scoverage.ScoverageKeys
import sbt._
import sbt.Keys._
import uk.gov.hmrc.DefaultBuildSettings.targetJvm
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion
import bloop.integrations.sbt.BloopDefaults
  
Global / bloopAggregateSourceDependencies := true
Global / bloopExportJarClassifiers := Some(Set("sources"))

val libName = "api-platform-application-domain"

lazy val scala2_13 = "2.13.12"

ThisBuild / majorVersion     := 0
ThisBuild / isPublicArtefact := true
ThisBuild / scalaVersion     := scala2_13

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val library = Project(s"$libName-root", file("."))
  .settings(
    publish / skip := true
  )
  .aggregate(
    apiPlatformApplicationDomain, apiPlatformApplicationDomainFixtures, apiPlatformApplicationDomainTest
  )

lazy val apiPlatformApplicationDomain = Project(libName, file(libName))
  .settings(
    libraryDependencies ++= LibraryDependencies.applicationDomain,
    ScoverageSettings(),
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-eT"),
  )
  .disablePlugins(JUnitXmlReportPlugin)


lazy val apiPlatformApplicationDomainFixtures = Project(s"$libName-fixtures", file(s"$libName-fixtures"))
  .dependsOn(
    apiPlatformApplicationDomain % "compile"
  )
  .settings(
    libraryDependencies ++= LibraryDependencies.root,
    ScoverageKeys.coverageEnabled := false,
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-eT"),
  )
  .disablePlugins(JUnitXmlReportPlugin)


lazy val apiPlatformApplicationDomainTest = Project(s"$libName-test", file(s"$libName-test"))
  .dependsOn(
    apiPlatformApplicationDomain,
    apiPlatformApplicationDomainFixtures
  )
  .settings(
    publish / skip := true,
    libraryDependencies ++= LibraryDependencies.root,
    ScoverageSettings(),
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-eT"),
  )
  .disablePlugins(JUnitXmlReportPlugin)


  commands ++= Seq(
    Command.command("run-all-tests") { state => "test" :: state },
    Command.command("clean-and-test") { state => "clean" :: "run-all-tests" :: state },
    Command.command("pre-commit") { state => "clean" :: "scalafmtAll" :: "scalafixAll" ::"coverage" :: "run-all-tests" :: "coverageOff" :: "coverageAggregate" :: state }
  )
