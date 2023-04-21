package graphql.types.task

import graphql.types.util.{ Natural, Positive }
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, OutputType }
import io.scalaland.chimney.dsl._

@JsonCodec
case class Progress(
    reached: Natural,
    reachable: Positive
)

object Progress {

  implicit val toInternal: Transformer[Progress, services.task.Progress] = progress =>
    services.task.Progress.fraction(
      reachable = progress.reachable.transformInto[math.Positive],
      reached = progress.reached.transformInto[spire.math.Natural]
    )

  implicit val fromInternal: Transformer[services.task.Progress, Progress] = progress =>
    Progress(
      reached = progress.reached.transformInto[Natural],
      reachable = progress.reachable.transformInto[Positive]
    )

  implicit lazy val progressOutputType: OutputType[Progress] = deriveObjectType[Unit, Progress]()

  implicit val progressInputObjectType: InputObjectType[Progress] = deriveInputObjectType[Progress](
    InputObjectTypeName("ProgressInput")
  )

  implicit lazy val progressFromInput: FromInput[Progress] = circeDecoderFromInput[Progress]
}
