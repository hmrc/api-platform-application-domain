import scoverage.ScoverageKeys
  
object ScoverageSettings {
  def apply() = Seq(
    ScoverageKeys.coverageExcludedPackages := Seq(
      "<empty>",
      """uk\.gov\.hmrc\.BuildInfo""",
    ).mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 90.25,    // Push back to 97+ when scoverage exclusion works in scala 3
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}
