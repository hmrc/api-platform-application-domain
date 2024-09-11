import sbt._

object LibraryDependencies {
  def apply() = compileDependencies ++ testDependencies

  val commonDomainVersion = "0.17.0-SNAPSHOT"

  lazy val compileDependencies = Seq(
    "uk.gov.hmrc"             %% "api-platform-common-domain"     % commonDomainVersion,
    "com.github.t3hnar"       %% "scala-bcrypt"                   % "4.1",
    "com.typesafe"             % "config"                         % "1.4.2",
    "commons-validator"        % "commons-validator"              % "1.7"
  )

  lazy val testDependencies = Seq(
    "org.scalactic"           %% "scalactic"                        % "3.2.14",
    "org.scalatestplus"       %% "mockito-5-12"                     % "3.2.19.0",
    "uk.gov.hmrc"             %% "api-platform-test-common-domain"  % commonDomainVersion
  ).map(_ % "test")
}
