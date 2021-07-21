package graphql.mutations

import cats.effect.IO
import errors.ServerError
import graphql.HasGraphQLServices.syntax._
import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.dashboard.{ Dashboard, DashboardCreation, DashboardId, DashboardUpdate }
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait DashboardMutation extends HasGraphQLServices with HasLoggedInUser {

  import ioImplicits._

  @GraphQLField
  def createDashboard(dashboardCreation: DashboardCreation): Future[Dashboard] = {
    withUser { userId =>
      graphQLServices.dashboardService
        .create(userId.toInternal, dashboardCreation.toInternal)
    }
      .map(_.fromInternal[Dashboard])
      .unsafeToFuture()
      .handleServerError
  }

  def updateDashboard(dashboardId: DashboardId, dashboardUpdate: DashboardUpdate): Future[Dashboard] =
    validateDashboardWriteAccess(dashboardId) { _ =>
      graphQLServices.dashboardService
        .update(
          dashboardId = dashboardId.toInternal,
          dashboardUpdate = dashboardUpdate.toInternal
        )
        .map(_.fromInternal[Dashboard])
    }

  def deleteDashboard = ???

  def addProjectToDashboard = ???

  def adjustWeightsOnDashboard = ???

  def removeProjectFromDashboard = ???

  /*  TODO: Dashboard write access allows to modify the dashboard alone.
   *   Unclear:
   *     - Can a user with dashboard write access add a project without project read access?
   *       Likely: No. Such a project should not be visible to the user, and an additional block acts as a support.
   *     - Is there a problem if a user adds a project while having project read access, but then loses read access?
   *       Likely: No, but dashboard delivery (Query) should make clear that projects may be missing.
   * */
  private def validateDashboardWriteAccess[A](
      dashboardId: DashboardId
  )(
      f: services.user.UserId => IO[ServerError.Valid[A]]
  ): Future[A] = {
    validateDashboardAccess(
      dashboardService = graphQLServices.dashboardService,
      dashboardId = dashboardId,
      accessorsOf = _.writeAccessors.accessors
    )((user, _) => f(user))
  }

}
