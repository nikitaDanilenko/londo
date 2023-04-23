package graphql

import services.dashboard.DashboardService
import services.loginThrottle.LoginThrottleService
import services.project.ProjectService
import services.session.SessionService
import services.task.TaskService
import services.user.UserService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

case class GraphQLServices @Inject() (
    userService: UserService,
    projectService: ProjectService,
    taskService: TaskService,
    dashboardService: DashboardService,
    sessionService: SessionService,
    loginThrottleService: LoginThrottleService
)(implicit
    val executionContext: ExecutionContext
)
