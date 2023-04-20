package services.task.reference

import cats.effect.IO
import db.{ ProjectId, ReferenceTaskId }
import io.scalaland.chimney.dsl.TransformerOps
import utils.random.RandomGenerator
import utils.transformer.implicits._

case class ReferenceTaskCreation(
    projectReferenceId: ProjectId
)

object ReferenceTaskCreation {

  def create(referenceTaskCreation: ReferenceTaskCreation): IO[ReferenceTask] =
    RandomGenerator.randomUUID.map { uuid =>
      ReferenceTask(
        id = uuid.transformInto[ReferenceTaskId],
        projectReferenceId = referenceTaskCreation.projectReferenceId
      )
    }

}
