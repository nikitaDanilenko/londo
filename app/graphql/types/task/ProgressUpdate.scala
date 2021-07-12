package graphql.types.task

import graphql.types.ToInternal
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.marshalling.FromInput
import sangria.schema.InputObjectType
import spire.math.Natural
import utils.json.CirceUtil.instances._
import utils.graphql.SangriaUtil.instances._

@JsonCodec
case class ProgressUpdate(
    reached: Natural,
    reachable: Natural
)

object ProgressUpdate {

  implicit val progressUpdateToInternal: ToInternal[ProgressUpdate, services.task.ProgressUpdate] = progressUpdate =>
    services.task.ProgressUpdate(
      reached = progressUpdate.reached,
      reachable = progressUpdate.reachable
    )

  implicit val progressUpdateInputType: InputObjectType[ProgressUpdate] = deriveInputObjectType[ProgressUpdate]()

  implicit lazy val progressUpdateFromInput: FromInput[ProgressUpdate] = circeDecoderFromInput[ProgressUpdate]
}
