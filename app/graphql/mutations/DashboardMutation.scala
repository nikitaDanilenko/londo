package graphql.mutations

import graphql.{ HasGraphQLServices, HasLoggedInUser }

trait DashboardMutation extends HasGraphQLServices with HasLoggedInUser {

  import ioImplicits._

  def createDashboard

  def updateDashboard

  def deleteDashboard

  def addProjectToDashboard

  def adjustWeightsOnDashboard

  def removeProjectFromDashboard

}
