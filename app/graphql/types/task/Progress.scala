package graphql.types.task

import graphql.types.FromAndToInternal
import graphql.types.task.TaskCreation.PlainCreation
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, OutputType }
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

  implicit val progressInputObjectType: InputObjectType[Progress] = deriveInputObjectType[Progress](
    InputObjectTypeName("ProgressInput")
  )

  implicit lazy val progressFromInput: FromInput[Progress] = circeDecoderFromInput[Progress]
}
