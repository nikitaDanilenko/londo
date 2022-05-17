package graphql.types.task

import graphql.types.FromAndToInternal
import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.util.{ Natural, Positive }
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, OutputType }

@JsonCodec
case class Progress(
    reached: Natural,
    reachable: Positive
)

object Progress {

  implicit lazy val progressFromAndToInternal: FromAndToInternal[Progress, services.task.Progress] =
    FromAndToInternal.create(
      progress =>
        Progress(
          reached = progress.reached.fromInternal,
          reachable = progress.reachable.fromInternal
        ),
      progress =>
        services.task.Progress.fraction(
          reachable = progress.reachable.toInternal,
          reached = progress.reached.toInternal
        )
    )

  implicit lazy val progressOutputType: OutputType[Progress] = deriveObjectType[Unit, Progress]()

  implicit val progressInputObjectType: InputObjectType[Progress] = deriveInputObjectType[Progress](
    InputObjectTypeName("ProgressInput")
  )

  implicit lazy val progressFromInput: FromInput[Progress] = circeDecoderFromInput[Progress]
}
