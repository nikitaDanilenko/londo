package graphql.types.task

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType
import spire.math.Natural
import utils.json.CirceUtil.instances._
import utils.graphql.SangriaUtil.instances._

@JsonCodec
case class Progress(
    reached: Natural,
    reachable: Natural
)

object Progress {

  def fromInternal(progress: services.task.Progress): Progress =
    Progress(
      reached = progress.reached,
      reachable = progress.reachable
    )

  implicit lazy val progressOutputType: OutputType[Progress] = deriveObjectType[Unit, Progress]()
}
