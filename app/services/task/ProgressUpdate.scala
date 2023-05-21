package services.task

import math.Positive
import spire.math.Natural

case class ProgressUpdate(
    reached: Natural,
    reachable: Positive
)

object ProgressUpdate {

  def update(task: Task, progressUpdate: ProgressUpdate): Task =
    task.copy(progress =
      Progress.fraction(
        reachable = progressUpdate.reachable,
        reached = progressUpdate.reached
      )
    )

}
