package graphql

import services.dashboard.DashboardService
import services.dashboardEntry.DashboardEntryService
import services.email.EmailService
import services.loginThrottle.LoginThrottleService
import services.project.ProjectService
import services.session.SessionService
import services.simulation.SimulationService
import services.task.TaskService
import services.user.UserService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

case class GraphQLServices @Inject() (
    userService: UserService,
    projectService: ProjectService,
    taskService: TaskService,
    dashboardService: DashboardService,
    dashboardEntryService: DashboardEntryService,
    sessionService: SessionService,
    loginThrottleService: LoginThrottleService,
    emailService: EmailService,
    simulationService: SimulationService
)(implicit
    val executionContext: ExecutionContext
)
