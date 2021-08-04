package services.dashboard

import cats.effect.IO
import services.access.{ Access, AccessKind, Accessors }
import services.user.UserId
import utils.random.RandomGenerator

case class DashboardCreation(
    header: String,
    description: Option[String],
    readAccessors: Accessors.Representation,
    writeAccessors: Accessors.Representation
)

object DashboardCreation {

  def create(userId: UserId, dashboardCreation: DashboardCreation): IO[Dashboard] =
    RandomGenerator.randomUUID.map { uuid =>
      Dashboard(
        id = DashboardId(uuid),
        projects = Vector.empty,
        header = dashboardCreation.header,
        description = dashboardCreation.description,
        userId = userId,
        readAccessors = Access[AccessKind.Read](Accessors.fromRepresentation(dashboardCreation.readAccessors)),
        writeAccessors = Access[AccessKind.Write](Accessors.fromRepresentation(dashboardCreation.writeAccessors))
      )
    }

}
