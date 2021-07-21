package graphql.types.dashboard

import graphql.types.ToInternal
import graphql.types.ToInternal.syntax._
import graphql.types.project.Accessors
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class DashboardCreation(
    header: String,
    description: Option[String],
    readAccessors: Accessors,
    writeAccessors: Accessors
)

object DashboardCreation {

  implicit val projectCreationToInternal: ToInternal[DashboardCreation, services.dashboard.DashboardCreation] =
    projectCreation =>
      services.dashboard.DashboardCreation(
        header = projectCreation.header,
        description = projectCreation.description,
        readAccessors = projectCreation.readAccessors.toInternal,
        writeAccessors = projectCreation.writeAccessors.toInternal
      )

  implicit val projectCreationInputObjectType: InputObjectType[DashboardCreation] =
    deriveInputObjectType[DashboardCreation]()

  implicit lazy val projectCreationFromInput: FromInput[DashboardCreation] = circeDecoderFromInput[DashboardCreation]

}
