import sbt._

object LibraryDependencies {
  def apply() = compileDependencies ++ testDependencies
  
  val commonDomainVersion = "0.17.0-SNAPSHOT"
  val monocleVersion = "3.1.0"

  lazy val compileDependencies = Seq(
    "uk.gov.hmrc"             %% "api-platform-common-domain"     % commonDomainVersion,
    "com.github.t3hnar"       %% "scala-bcrypt"                   % "4.1",
    "com.typesafe"             % "config"                         % "1.4.2",
    "commons-validator"        % "commons-validator"              % "1.7",
    "dev.optics"              %% "monocle-core"                   % monocleVersion,
    "dev.optics"              %% "monocle-macro"                  % monocleVersion
  )

  lazy val testDependencies = Seq(
    "org.scalactic"           %% "scalactic"                            % "3.2.14",
    "com.vladsch.flexmark"     % "flexmark-all"                         % "0.62.2",
    "org.mockito"             %% "mockito-scala-scalatest"              % "1.17.29",
    "org.scalatest"           %% "scalatest"                            % "3.2.17",
    "uk.gov.hmrc"             %% "api-platform-common-domain-fixtures"  % commonDomainVersion
  )

  lazy val applicationDomain = compileDependencies ++ testDependencies.map(_ % "test")

  lazy val root = compileDependencies ++ testDependencies
  
}
