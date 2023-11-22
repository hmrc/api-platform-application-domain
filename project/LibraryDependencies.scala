import sbt._

object LibraryDependencies {
  def apply() = compileDependencies ++ testDependencies

  lazy val compileDependencies = Seq(
    "uk.gov.hmrc"             %% "api-platform-common-domain"     % "0.5.0",
    "com.github.t3hnar"       %% "scala-bcrypt"                   % "4.1",
    "com.typesafe"             % "config"                         % "1.4.2"
  )

  lazy val testDependencies = Seq(
    "org.scalactic"           %% "scalactic"                      % "3.2.14"            % "test",
    "com.vladsch.flexmark"     % "flexmark-all"                   % "0.62.2"            % "test",
    "org.mockito"             %% "mockito-scala-scalatest"        % "1.17.29"           % "test",
    "org.scalatest"           %% "scalatest"                      % "3.2.17"
  )
}
