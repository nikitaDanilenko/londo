package graphql.mutations.dashboard

import cats.data.EitherT
import graphql.HasGraphQLServices.syntax._
import graphql.mutations.dashboard.inputs._
import graphql.types.dashboard.Dashboard
import graphql.types.dashboardEntry.DashboardEntry
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
  def createDashboardEntry(input: CreateDashboardEntryInput): Future[DashboardEntry] =
    withUserId { userId =>
      EitherT(
        graphQLServices.dashboardEntryService
          .create(
            userId = userId,
            dashboardId = input.dashboardId.transformInto[db.DashboardId],
            creation = input.dashboardEntryCreation.transformInto[services.dashboardEntry.Creation]
          )
      )
        .map(_.transformInto[DashboardEntry])
        .value
        .handleServerError
    }

  @GraphQLField
  def deleteDashboardEntry(input: DeleteDashboardEntryInput): Future[Boolean] =
    withUserId { userId =>
      graphQLServices.dashboardEntryService
        .delete(
          userId = userId,
          key = db.daos.dashboardEntry.DashboardEntryKey(
            input.dashboardId.transformInto[db.DashboardId],
            input.projectId.transformInto[db.ProjectId]
          )
        )
        .handleServerError
    }
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
