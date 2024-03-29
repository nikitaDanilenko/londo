package graphql.types.project

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.schema.{ InputObjectType, ObjectType }
import utils.graphql.SangriaUtil.instances._
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

import java.util.UUID

@JsonCodec
case class ProjectId(uuid: UUID)

object ProjectId {

  implicit val toInternal: Transformer[ProjectId, db.ProjectId] =
    _.uuid.transformInto[db.ProjectId]

  implicit val fromInternal: Transformer[db.ProjectId, ProjectId] =
    ProjectId(_)

  implicit val objectType: ObjectType[Unit, ProjectId] = deriveObjectType[Unit, ProjectId]()

  implicit val inputObjectType: InputObjectType[ProjectId] = deriveInputObjectType[ProjectId](
    InputObjectTypeName("ProjectIdInput")
  )

}
