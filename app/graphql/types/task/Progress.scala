package graphql.types.task

import graphql.types.FromAndToInternal
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

  implicit lazy val progressFromAndToInternal: FromAndToInternal[Progress, services.task.Progress] =
    FromAndToInternal.create(
      progress =>
        Progress(
          reached = progress.reached,
          reachable = progress.reachable
        ),
      progress =>
        services.task.Progress.fraction(
          reachable = progress.reachable,
          reached = progress.reached
        )
    )

  implicit lazy val progressOutputType: OutputType[Progress] = deriveObjectType[Unit, Progress]()
}
