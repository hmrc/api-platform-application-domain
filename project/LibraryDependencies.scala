import sbt._

object LibraryDependencies {
  val commonDomainVersion = "1.3.0"
  val monocleVersion = "3.1.0"

  def domain(scalaVersion: String) = 
    compileDependencies(scalaVersion) ++
    fixturesDependencies.map(_ % "provided")

  def fixtures(scalaVersion: String) =
    compileDependencies(scalaVersion) ++
    fixturesDependencies.map(_ % "provided")

  def tests(scalaVersion: String) = 
    compileDependencies(scalaVersion) ++
    fixturesDependencies.map(_ % "test") ++ 
    testDependencies
  
  private def compileDependencies(scalaVersion: String) = Seq(
    "uk.gov.hmrc"             %% "api-platform-common-domain"     % commonDomainVersion % "provided",
    "com.typesafe"             % "config"                         % "1.4.2",
    "commons-validator"        % "commons-validator"              % "1.9.0",
    "dev.optics"              %% "monocle-core"                   % monocleVersion,
    "dev.optics"              %% "monocle-macro"                  % monocleVersion
    ) ++ (
      CrossVersion.partialVersion(scalaVersion) match {
        case Some((2,_)) => Seq("com.github.t3hnar"       %% "scala-bcrypt" % "4.3.0")
        case _           => Seq("de.svenkubiak"            % "jBCrypt"      % "0.4.3")
      }
    )

  private def fixturesDependencies = Seq(
    "uk.gov.hmrc"             %% "api-platform-common-domain-fixtures" % commonDomainVersion
  )

  private def testDependencies = Seq.empty[ModuleID]
    .map(_ % "test") 
}
