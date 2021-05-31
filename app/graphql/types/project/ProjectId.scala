package graphql.types.project

import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }
import utils.graphql.SangriaUtil.instances._

import java.util.UUID

@JsonCodec
case class ProjectId(uuid: UUID)

object ProjectId {

  def fromInternal(projectId: services.project.ProjectId): ProjectId =
    ProjectId(
      uuid = projectId.uuid
    )

  def toInternal(projectId: ProjectId): services.project.ProjectId =
    services.project.ProjectId(
      uuid = projectId.uuid
    )

  implicit val projectIdObjectType: ObjectType[Unit, ProjectId] = deriveObjectType[Unit, ProjectId]()

  implicit val projectIdInputObjectType: InputObjectType[ProjectId] = deriveInputObjectType[ProjectId](
    InputObjectTypeName("ProjectIdInput")
  )

  implicit lazy val projectIdFromInput: FromInput[ProjectId] = circeDecoderFromInput[ProjectId]
}
