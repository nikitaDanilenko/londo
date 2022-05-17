package graphql.types.project

import graphql.types.FromInternal
import graphql.types.FromInternal.syntax._
import graphql.types.util.Positive
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec
case class WeightedProject(
    resolvedProject: ResolvedProject,
    weight: Positive
)

object WeightedProject {

  implicit val weightedProjectFromInternal: FromInternal[WeightedProject, services.project.WeightedProject] =
    weightedProject =>
      WeightedProject(
        resolvedProject = weightedProject.resolvedProject.fromInternal,
        weight = weightedProject.weight.fromInternal
      )

  implicit val weightedProjectObjectType: ObjectType[Unit, WeightedProject] = deriveObjectType[Unit, WeightedProject]()

}
