package modules

import play.api.inject.Binding
import play.api.{ Configuration, Environment }
import services.dashboard.DashboardService
import services.dashboardEntry.DashboardEntryService
import services.loginThrottle.LoginThrottleService
import services.project.ProjectService
import services.session.SessionService
import services.simulation.SimulationService
import services.task.TaskService
import services.user.UserService

class ApplicationModule extends play.api.inject.Module {

  override def bindings(environment: Environment, configuration: Configuration): collection.Seq[Binding[_]] = {
    val settings = Seq(
      bind[db.daos.dashboard.DAO].toInstance(db.daos.dashboard.DAO.instance),
      bind[db.daos.dashboardEntry.DAO].toInstance(db.daos.dashboardEntry.DAO.instance),
      bind[db.daos.loginThrottle.DAO].toInstance(db.daos.loginThrottle.DAO.instance),
      bind[db.daos.project.DAO].toInstance(db.daos.project.DAO.instance),
      bind[db.daos.session.DAO].toInstance(db.daos.session.DAO.instance),
      bind[db.daos.task.DAO].toInstance(db.daos.task.DAO.instance),
      bind[db.daos.user.DAO].toInstance(db.daos.user.DAO.instance),
      bind[db.daos.simulation.DAO].toInstance(db.daos.simulation.DAO.instance),
      bind[DashboardService.Companion].to[services.dashboard.Live.Companion],
      bind[DashboardService].to[services.dashboard.Live],
      bind[DashboardEntryService.Companion].to[services.dashboardEntry.Live.Companion],
      bind[DashboardEntryService].to[services.dashboardEntry.Live],
      bind[LoginThrottleService.Companion].to[services.loginThrottle.Live.Companion],
      bind[LoginThrottleService].to[services.loginThrottle.Live],
      bind[ProjectService.Companion].to[services.project.Live.Companion],
      bind[ProjectService].to[services.project.Live],
      bind[SessionService.Companion].to[services.session.Live.Companion],
      bind[SessionService].to[services.session.Live],
      bind[TaskService.Companion].to[services.task.Live.Companion],
      bind[TaskService].to[services.task.Live],
      bind[UserService.Companion].to[services.user.Live.Companion],
      bind[UserService].to[services.user.Live],
      bind[SimulationService.Companion].to[services.simulation.Live.Companion],
      bind[SimulationService].to[services.simulation.Live]
    )
    settings
  }

}
