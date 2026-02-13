import sbt._

object LibraryDependencies {
  def apply(scalaVersion: String) = compileDependencies(scalaVersion) ++ testDependencies(scalaVersion)

  val commonDomainVersion = "1.0.0-SNAPSHOT"
  val monocleVersion = "3.1.0"

  def applicationDomain(scalaVersion: String) = compileDependencies(scalaVersion) ++ testDependencies(scalaVersion).map(_ % "test")

  def root(scalaVersion: String) = compileDependencies(scalaVersion) ++ testDependencies(scalaVersion)

  def compileDependencies(scalaVersion: String) = Seq(
    "uk.gov.hmrc"             %% "api-platform-common-domain"     % commonDomainVersion,
    "com.typesafe"             % "config"                         % "1.4.2",
    "commons-validator"        % "commons-validator"              % "1.9.0",
    "dev.optics"              %% "monocle-core"                   % monocleVersion,
    "dev.optics"              %% "monocle-macro"                  % monocleVersion
    ) ++ (
      CrossVersion.partialVersion(scalaVersion) match {
        case Some((2,_)) => Seq("com.github.t3hnar"       %% "scala-bcrypt"                   % "4.1")
        case _           => Seq.empty   // pending some rewrite
      }
    )

  def testDependencies(scalaVersion: String) = Seq(
    "org.scalactic"           %% "scalactic"                            % "3.2.14",
    "com.vladsch.flexmark"     % "flexmark-all"                         % "0.62.2",
    "uk.gov.hmrc"             %% "api-platform-common-domain-fixtures"  % commonDomainVersion,
    "org.scalatest"           %% "scalatest"                            % "3.2.19",
    ) ++ (
      CrossVersion.partialVersion(scalaVersion) match {
        case Some((2,_)) => Seq("org.mockito" %% "mockito-scala-scalatest" % "2.0.0")
        case _           => Seq.empty   // scalatest brings in scalatest-mockito transiently
      }
    )
}
