package graphql.mutations.project.inputs

import graphql.types.util.{Natural, Positive}
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType
import services.task

@JsonCodec
case class ProgressUpdate(
    reached: Natural,
    reachable: Positive
)

object ProgressUpdate {

  implicit val toInternal: Transformer[ProgressUpdate, task.ProgressUpdate] =
    Transformer
      .define[ProgressUpdate, task.ProgressUpdate]
      .buildTransformer

  implicit val progressUpdateInputType: InputObjectType[ProgressUpdate] = deriveInputObjectType[ProgressUpdate]()

  implicit lazy val progressUpdateFromInput: FromInput[ProgressUpdate] = circeDecoderFromInput[ProgressUpdate]
}
