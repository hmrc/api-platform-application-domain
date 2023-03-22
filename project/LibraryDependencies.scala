import sbt._

object LibraryDependencies {
  def apply() = compileDependencies ++ testDependencies

  lazy val compileDependencies = Seq(
    "com.typesafe.play"       %% "play-json"                      % "2.9.2",
    "uk.gov.hmrc"             %% "play-json-union-formatter"      % "1.18.0-play-28"
  )

  lazy val testDependencies = Seq(
    "org.scalactic"           %% "scalactic"                      % "3.2.14"            % "test",
    "org.scalatest"           %% "scalatest"                      % "3.2.14"            % "test",
    "com.vladsch.flexmark"     % "flexmark-all"                   % "0.62.2"            % "test",
    "org.mockito"             %% "mockito-scala-scalatest"        % "1.7.1"             % "test"
  )
}
