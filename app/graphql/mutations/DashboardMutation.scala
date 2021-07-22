package graphql.mutations

import cats.effect.IO
import errors.ServerError
import graphql.HasGraphQLServices.syntax._
import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.dashboard.{ Dashboard, DashboardCreation, DashboardId, DashboardUpdate, ProjectWeightOnDashboard }
import graphql.types.project.ProjectId
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField
import spire.math.Natural
import cats.syntax.traverse._

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

  @GraphQLField
  def updateDashboard(dashboardId: DashboardId, dashboardUpdate: DashboardUpdate): Future[Dashboard] =
    validateDashboardWriteAccess(dashboardId) { _ =>
      graphQLServices.dashboardService
        .update(
          dashboardId = dashboardId.toInternal,
          dashboardUpdate = dashboardUpdate.toInternal
        )
        .map(_.fromInternal[Dashboard])
    }

  @GraphQLField
  def deleteDashboard(dashboardId: DashboardId): Future[Dashboard] =
    validateDashboardWriteAccess(dashboardId) { _ =>
      graphQLServices.dashboardService
        .delete(dashboardId.toInternal)
        .map(_.fromInternal[Dashboard])
    }

  @GraphQLField
  def addProjectToDashboard(dashboardId: DashboardId, projectId: ProjectId, weight: Natural): Future[Dashboard] =
    for {
      _ <- validateDashboardWriteAccess(dashboardId) { _ => IO.pure(ServerError.valid(())) }
      dashboard <- validateProjectAccess(graphQLServices.projectService, projectId, _.readAccessors.accessors) {
        (_, _) =>
          graphQLServices.dashboardService
            .addProject(
              dashboardId = dashboardId.toInternal,
              projectId = projectId.toInternal,
              weight = weight
            )
      }
    } yield dashboard.fromInternal[Dashboard]

  @GraphQLField
  def adjustWeightsOnDashboard(
      dashboardId: DashboardId,
      projectWeightsOnDashboard: Seq[ProjectWeightOnDashboard]
  ): Future[Dashboard] =
    // TODO: The below implementation seems too convoluted.
    for {
      _ <- validateDashboardWriteAccess(dashboardId)(_ => IO.pure(ServerError.valid(())))
      validatedProjectWeightsOnDashboard <- projectWeightsOnDashboard.traverse { projectWeightsOnDashboard =>
        validateProjectAccess(
          graphQLServices.projectService,
          projectWeightsOnDashboard.projectId,
          _.readAccessors.accessors
        ) { (_, _) =>
          IO.pure(ServerError.valid(projectWeightsOnDashboard))
        }
      }
      dashboard <-
        graphQLServices.dashboardService
          .setWeights(
            dashboardId.toInternal,
            validatedProjectWeightsOnDashboard.map(_.toInternal[services.dashboard.ProjectWeightOnDashboard])
          )
          .map(_.fromInternal[Dashboard])
          .unsafeToFuture()
          .handleServerError
    } yield dashboard

  @GraphQLField
  def removeProjectFromDashboard(dashboardId: DashboardId, projectId: ProjectId): Future[Dashboard] =
    for {
      _ <- validateDashboardWriteAccess(dashboardId) { _ => IO.pure(ServerError.valid(())) }
      dashboard <- validateProjectAccess(graphQLServices.projectService, projectId, _.readAccessors.accessors) {
        (_, _) =>
          graphQLServices.dashboardService
            .removeProject(
              dashboardId = dashboardId.toInternal,
              projectId = projectId.toInternal
            )
      }
    } yield dashboard.fromInternal[Dashboard]

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
