package graphql.queries

import graphql.{ HasGraphQLServices, HasLoggedInUser }

trait DashboardQuery extends HasGraphQLServices with HasLoggedInUser {

  import ioImplicits._

  def findDashboard

  def dashboardProgressOf

}
