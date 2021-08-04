package graphql.types.project

import graphql.types.FromInternal
import graphql.types.FromInternal.syntax._
import io.circe.generic.JsonCodec
import math.Positive
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType
import utils.graphql.SangriaUtil.instances._
import utils.json.CirceUtil.instances._

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
        weight = weightedProject.weight
      )

  implicit val weightedProjectObjectType: ObjectType[Unit, WeightedProject] = deriveObjectType[Unit, WeightedProject]()

}
