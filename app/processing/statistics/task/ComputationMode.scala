package processing.statistics.task

sealed trait ComputationMode

object ComputationMode {
  case object Total    extends ComputationMode
  case object Counting extends ComputationMode
}
