package graphql.mutations.dashboard

import graphql.mutations.dashboard.inputs.CreateDashboardInput
import graphql.types.dashboard.{ Dashboard, DashboardCreation, DashboardId, DashboardUpdate }
import graphql.types.project.ProjectId
import graphql.types.util.Natural
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait Mutation extends HasGraphQLServices with HasLoggedInUser {

  @GraphQLField
  def createDashboard(input: CreateDashboardInput): Future[Dashboard] = ???
//    withUser { userId =>
//      graphQLServices.dashboardService
//        .create(userId.toInternal, dashboardCreation.toInternal)
//    }
//      .map(_.fromInternal[Dashboard])
//      .unsafeToFuture()
//      .handleServerError

  @GraphQLField
  def updateDashboard(dashboardId: DashboardId, dashboardUpdate: DashboardUpdate): Future[Dashboard] = ???
//    validateDashboardWriteAccess(dashboardId) { _ =>
//      graphQLServices.dashboardService
//        .update(
//          id = dashboardId.toInternal,
//          dashboardUpdate = dashboardUpdate.toInternal
//        )
//        .map(_.fromInternal[Dashboard])
//    }

  @GraphQLField
  def deleteDashboard(dashboardId: DashboardId): Future[Dashboard] = ???
//    validateDashboardWriteAccess(dashboardId) { _ =>
//      graphQLServices.dashboardService
//        .delete(id = dashboardId.toInternal)
//        .map(_.fromInternal[Dashboard])
//    }

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
