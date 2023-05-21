package graphql.mutations.dashboard.inputs

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class DashboardEntryCreation(
    projectId: ProjectId
)

object DashboardEntryCreation {

  implicit val toInternal: Transformer[DashboardEntryCreation, services.dashboardEntry.Creation] =
    Transformer
      .define[DashboardEntryCreation, services.dashboardEntry.Creation]
      .buildTransformer

  implicit val inputObjectType: InputObjectType[DashboardEntryCreation] = deriveInputObjectType[DashboardEntryCreation]()
}
