package services.task.reference

import cats.effect.IO
import db.{ ProjectId, ReferenceTaskId }
import io.scalaland.chimney.dsl.TransformerOps
import utils.date.DateUtil
import utils.random.RandomGenerator
import utils.transformer.implicits._

case class ReferenceTaskCreation(
    projectReferenceId: ProjectId
)

object ReferenceTaskCreation {

  def create(referenceTaskCreation: ReferenceTaskCreation): IO[ReferenceTask] =
    for {
      id  <- RandomGenerator.randomUUID.map(_.transformInto[ReferenceTaskId])
      now <- DateUtil.now
    } yield ReferenceTask(
      id = id,
      projectReferenceId = referenceTaskCreation.projectReferenceId,
      createdAt = now,
      updatedAt = None
    )

}
