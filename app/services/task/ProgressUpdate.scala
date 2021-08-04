package services.task

import math.Positive
import spire.math.Natural

case class ProgressUpdate(
    reached: Natural,
    reachable: Positive
)

object ProgressUpdate {

  def applyToTask(plainTask: Task.Plain, progressUpdate: ProgressUpdate): Task.Plain =
    plainTask.copy(progress =
      Progress.fraction(
        reachable = progressUpdate.reachable,
        reached = progressUpdate.reached
      )
    )

}
