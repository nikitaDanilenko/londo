package graphql.mutations

import graphql.HasGraphQLServices.syntax._
import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.dashboard.{ Dashboard, DashboardCreation }
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

  def updateDashboard = ???

  def deleteDashboard = ???

  def addProjectToDashboard = ???

  def adjustWeightsOnDashboard = ???

  def removeProjectFromDashboard = ???

}
