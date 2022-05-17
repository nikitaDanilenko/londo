package graphql.types.task

import graphql.types.ToInternal
import graphql.types.ToInternal.syntax._
import graphql.types.util.{ Natural, Positive }
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class ProgressUpdate(
    reached: Natural,
    reachable: Positive
)

object ProgressUpdate {

  implicit val progressUpdateToInternal: ToInternal[ProgressUpdate, services.task.ProgressUpdate] = progressUpdate =>
    services.task.ProgressUpdate(
      reached = progressUpdate.reached.toInternal,
      reachable = progressUpdate.reachable.toInternal
    )

  implicit val progressUpdateInputType: InputObjectType[ProgressUpdate] = deriveInputObjectType[ProgressUpdate]()

  implicit lazy val progressUpdateFromInput: FromInput[ProgressUpdate] = circeDecoderFromInput[ProgressUpdate]
}
