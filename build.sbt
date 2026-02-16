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

val scala2_13 = "2.13.16"
val scala3 = "3.3.7"

inThisBuild(
  List(
    majorVersion := 1,
    scalaVersion := scala2_13,
    isPublicArtefact := true,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
  )
)

lazy val sharedScalacOptions =
  Seq("-encoding", "UTF-8", "-Wunused:imports,privates,locals")

lazy val scala2Options = sharedScalacOptions ++
  Seq("-explaintypes")

lazy val scala3Options = sharedScalacOptions ++
  Seq("-explain")

lazy val commonSettings = Seq(
  scalafixConfig := {
    val base = (ThisBuild / baseDirectory).value
    val file =
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) => base / ".scalafix-scala3.conf"
        case _            => base / ".scalafix-scala2.conf"
      }
    Some(file)
  },

  scalafmtConfig := {
    val base = (ThisBuild / baseDirectory).value
    val file =
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) => base / ".scalafmt-scala3.conf"
        case _            => base / ".scalafmt-scala2.conf"
      }
    file
  },

  scalacOptions ++= 
    (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) => scala3Options
      case _            => scala2Options
    }),

    crossScalaVersions := Seq(scala3, scala2_13),
)

lazy val library = Project(s"$libName-root", file("."))
  .settings(
    commonSettings,
    crossScalaVersions := Nil,
    publish / skip := true,
    ScoverageSettings()
  )
  .aggregate(
    apiPlatformApplicationDomain, apiPlatformApplicationDomainFixtures, apiPlatformApplicationDomainTest
  )

lazy val apiPlatformApplicationDomain = Project(libName, file(libName))
  .settings(
    commonSettings,
    libraryDependencies ++= LibraryDependencies.applicationDomain(scalaVersion.value),
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-eT"),
  )
  .disablePlugins(JUnitXmlReportPlugin)


lazy val apiPlatformApplicationDomainFixtures = Project(s"$libName-fixtures", file(s"$libName-fixtures"))
  .dependsOn(
    apiPlatformApplicationDomain % "compile"
  )
  .settings(
    commonSettings,
    libraryDependencies ++= LibraryDependencies.root(scalaVersion.value),
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
    commonSettings,
    publish / skip := true,
    libraryDependencies ++= LibraryDependencies.root(scalaVersion.value),
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-eT")
  )
  .disablePlugins(JUnitXmlReportPlugin)

  commands ++= Seq(
  Command.command("run-all-tests") { state => "test" :: state },
  Command.command("coverage-test") { state => "coverage" :: "run-all-tests" :: "coverageOff" :: "coverageAggregate" :: state },
  Command.command("check") { state => "clean" :: "coverage-test" :: state },
  Command.command("all") { state => "clean" :: "scalafmtAll" :: "scalafixAll" :: "coverage-test" :: state },

  Command.command("clean-and-test") { state => "clean" :: "run-all-tests" :: state },

  // Coverage does not need compile !
  Command.command("pre-commit") { state => "clean" :: "scalafmtAll" :: "scalafixAll" :: "coverage-test" :: state }
  )
