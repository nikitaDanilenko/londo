package graphql.mutations.dashboard

import cats.data.EitherT
import graphql.HasGraphQLServices.syntax._
import graphql.mutations.dashboard.inputs.{ CreateDashboardInput, DeleteDashboardInput, UpdateDashboardInput }
import graphql.types.dashboard.{ Dashboard, DashboardId }
import graphql.types.project.ProjectId
import graphql.types.util.Natural
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import io.scalaland.chimney.dsl.TransformerOps
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait Mutation extends HasGraphQLServices with HasLoggedInUser {

  @GraphQLField
  def createDashboard(input: CreateDashboardInput): Future[Dashboard] =
    withUserId { userId =>
      EitherT(
        graphQLServices.dashboardService
          .create(
            ownerId = userId,
            creation = input.dashboardCreation.transformInto[services.dashboard.Creation]
          )
      )
        .map(_.transformInto[Dashboard])
        .value
        .handleServerError
    }

  @GraphQLField
  def updateDashboard(input: UpdateDashboardInput): Future[Dashboard] =
    withUserId { userId =>
      EitherT(
        graphQLServices.dashboardService
          .update(
            ownerId = userId,
            id = input.dashboardId.transformInto[db.DashboardId],
            update = input.dashboardUpdate.transformInto[services.dashboard.Update]
          )
      )
        .map(_.transformInto[Dashboard])
        .value
        .handleServerError
    }

  @GraphQLField
  def deleteDashboard(input: DeleteDashboardInput): Future[Boolean] =
    withUserId { userId =>
      graphQLServices.dashboardService
        .delete(
          ownerId = userId,
          id = input.dashboardId.transformInto[db.DashboardId]
        )
        .handleServerError
    }

  @GraphQLField
  def addProjectToDashboard(dashboardId: DashboardId, projectId: ProjectId, weight: Natural): Future[Dashboard] = ???
//    for {
//      _ <- validateDashboardWriteAccess(dashboardId) { _ => IO.pure(ServerError.result(())) }
//      dashboard <- validateProjectAccess(graphQLServices.projectService, projectId, _.readAccessors.accessors) {
//        (_, _) =>
//          graphQLServices.dashboardService
//            .addProject(
//              dashboardId = dashboardId.toInternal,
//              projectId = projectId.toInternal,
//              weight = weight.toInternal
//            )
//      }
//    } yield dashboard.fromInternal[Dashboard]

  @GraphQLField
  def removeProjectFromDashboard(dashboardId: DashboardId, projectId: ProjectId): Future[Dashboard] = ???
//    for {
//      _ <- validateDashboardWriteAccess(dashboardId) { _ => IO.pure(ServerError.result(())) }
//      dashboard <- validateProjectAccess(graphQLServices.projectService, projectId, _.readAccessors.accessors) {
//        (_, _) =>
//          graphQLServices.dashboardService
//            .removeProject(
//              dashboardId = dashboardId.toInternal,
//              projectId = projectId.toInternal
//            )
//      }
//    } yield dashboard.fromInternal[Dashboard]

}
