package graphql.types.project

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec
case class Project(
    id: ProjectId,
    name: String,
    description: Option[String]
)

object Project {

  implicit val fromInternal: Transformer[services.project.Project, Project] =
    Transformer
      .define[services.project.Project, Project]
      .buildTransformer

  implicit val projectObjectType: ObjectType[Unit, Project] = deriveObjectType[Unit, Project]()

}
