package graphql.types.dashboard

import graphql.types.FromAndToInternal
import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.project.ProjectId
import graphql.types.util.Natural
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }

@JsonCodec
case class ProjectWeightOnDashboard(
    projectId: ProjectId,
    weight: Natural
)

object ProjectWeightOnDashboard {

  implicit val projectWeightOnDashboardFromAndToInternal
      : FromAndToInternal[ProjectWeightOnDashboard, services.dashboard.ProjectWeightOnDashboard] =
    FromAndToInternal.create(
      internal =>
        ProjectWeightOnDashboard(
          projectId = internal.projectId.fromInternal,
          weight = internal.weight.fromInternal
        ),
      projectWeightOnDashboard =>
        services.dashboard.ProjectWeightOnDashboard(
          projectId = projectWeightOnDashboard.projectId.toInternal,
          weight = projectWeightOnDashboard.weight.toInternal
        )
    )

  implicit val projectWeightOnDashboardObjectType: ObjectType[Unit, ProjectWeightOnDashboard] =
    deriveObjectType[Unit, ProjectWeightOnDashboard]()

  implicit val projectWeightOnDashboardInputObjectType: InputObjectType[ProjectWeightOnDashboard] =
    deriveInputObjectType[ProjectWeightOnDashboard](
      InputObjectTypeName("ProjectWeightOnDashboardInput")
    )

  implicit lazy val projectWeightOnDashboardFromInput: FromInput[ProjectWeightOnDashboard] =
    circeDecoderFromInput[ProjectWeightOnDashboard]

}
