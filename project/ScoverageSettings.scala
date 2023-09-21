import scoverage.ScoverageKeys
  
object ScoverageSettings {
  def apply() = Seq(
    ScoverageKeys.coverageExcludedPackages := Seq(
      "<empty>",
      """uk\.gov\.hmrc\.BuildInfo""" ,
    ).mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 95.0,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}
