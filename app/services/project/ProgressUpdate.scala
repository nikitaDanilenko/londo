package services.project

import io.circe.generic.JsonCodec
import services.task.Task
import spire.math.Natural
import utils.json.CirceUtil.instances._

@JsonCodec
case class ProgressUpdate(
    reached: Natural,
    reachable: Natural
)

object ProgressUpdate {

  def applyToTask(task: Task, progressUpdate: ProgressUpdate): Task =
    task match {
      case plain: Task.Plain =>
        plain.copy(progress =
          Progress.fraction(
            reachable = progressUpdate.reachable,
            reached = progressUpdate.reached
          )
        )
      case projectReference: Task.ProjectReference => projectReference
    }

}
