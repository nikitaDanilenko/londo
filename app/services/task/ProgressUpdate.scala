package services.task

import spire.math.Natural

case class ProgressUpdate(
    reached: Natural,
    reachable: Natural
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
