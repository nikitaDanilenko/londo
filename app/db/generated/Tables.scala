package db.generated
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.PostgresProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(Dashboard.schema, DashboardEntry.schema, LoginThrottle.schema, Project.schema, Session.schema, Simulation.schema, Task.schema, User.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Dashboard
   *  @param id Database column id SqlType(uuid), PrimaryKey
   *  @param ownerId Database column owner_id SqlType(uuid)
   *  @param header Database column header SqlType(text)
   *  @param description Database column description SqlType(text), Default(None)
   *  @param visibility Database column visibility SqlType(text)
   *  @param createdAt Database column created_at SqlType(timestamptz)
   *  @param updatedAt Database column updated_at SqlType(timestamptz), Default(None) */
  case class DashboardRow(id: java.util.UUID, ownerId: java.util.UUID, header: String, description: Option[String] = None, visibility: String, createdAt: java.sql.Timestamp, updatedAt: Option[java.sql.Timestamp] = None)
  /** GetResult implicit for fetching DashboardRow objects using plain SQL queries */
  implicit def GetResultDashboardRow(implicit e0: GR[java.util.UUID], e1: GR[String], e2: GR[Option[String]], e3: GR[java.sql.Timestamp], e4: GR[Option[java.sql.Timestamp]]): GR[DashboardRow] = GR{
    prs => import prs._
    DashboardRow.tupled((<<[java.util.UUID], <<[java.util.UUID], <<[String], <<?[String], <<[String], <<[java.sql.Timestamp], <<?[java.sql.Timestamp]))
  }
  /** Table description of table dashboard. Objects of this class serve as prototypes for rows in queries. */
  class Dashboard(_tableTag: Tag) extends profile.api.Table[DashboardRow](_tableTag, "dashboard") {
    def * = (id, ownerId, header, description, visibility, createdAt, updatedAt) <> (DashboardRow.tupled, DashboardRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(ownerId), Rep.Some(header), description, Rep.Some(visibility), Rep.Some(createdAt), updatedAt)).shaped.<>({r=>import r._; _1.map(_=> DashboardRow.tupled((_1.get, _2.get, _3.get, _4, _5.get, _6.get, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)
    /** Database column owner_id SqlType(uuid) */
    val ownerId: Rep[java.util.UUID] = column[java.util.UUID]("owner_id")
    /** Database column header SqlType(text) */
    val header: Rep[String] = column[String]("header")
    /** Database column description SqlType(text), Default(None) */
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Default(None))
    /** Database column visibility SqlType(text) */
    val visibility: Rep[String] = column[String]("visibility")
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")
    /** Database column updated_at SqlType(timestamptz), Default(None) */
    val updatedAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("updated_at", O.Default(None))

    /** Foreign key referencing User (database name dashboard_owner_id_fk) */
    lazy val userFk = foreignKey("dashboard_owner_id_fk", ownerId, User)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Dashboard */
  lazy val Dashboard = new TableQuery(tag => new Dashboard(tag))

  /** Entity class storing rows of table DashboardEntry
   *  @param dashboardId Database column dashboard_id SqlType(uuid)
   *  @param projectId Database column project_id SqlType(uuid)
   *  @param createdAt Database column created_at SqlType(timestamptz) */
  case class DashboardEntryRow(dashboardId: java.util.UUID, projectId: java.util.UUID, createdAt: java.sql.Timestamp)
  /** GetResult implicit for fetching DashboardEntryRow objects using plain SQL queries */
  implicit def GetResultDashboardEntryRow(implicit e0: GR[java.util.UUID], e1: GR[java.sql.Timestamp]): GR[DashboardEntryRow] = GR{
    prs => import prs._
    DashboardEntryRow.tupled((<<[java.util.UUID], <<[java.util.UUID], <<[java.sql.Timestamp]))
  }
  /** Table description of table dashboard_entry. Objects of this class serve as prototypes for rows in queries. */
  class DashboardEntry(_tableTag: Tag) extends profile.api.Table[DashboardEntryRow](_tableTag, "dashboard_entry") {
    def * = (dashboardId, projectId, createdAt) <> (DashboardEntryRow.tupled, DashboardEntryRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(dashboardId), Rep.Some(projectId), Rep.Some(createdAt))).shaped.<>({r=>import r._; _1.map(_=> DashboardEntryRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column dashboard_id SqlType(uuid) */
    val dashboardId: Rep[java.util.UUID] = column[java.util.UUID]("dashboard_id")
    /** Database column project_id SqlType(uuid) */
    val projectId: Rep[java.util.UUID] = column[java.util.UUID]("project_id")
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")

    /** Primary key of DashboardEntry (database name dashboard_entry_pk) */
    val pk = primaryKey("dashboard_entry_pk", (dashboardId, projectId))

    /** Foreign key referencing Dashboard (database name dashboard_entry_dashboard_id_fk) */
    lazy val dashboardFk = foreignKey("dashboard_entry_dashboard_id_fk", dashboardId, Dashboard)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing Project (database name dashboard_entry_project_id_fk) */
    lazy val projectFk = foreignKey("dashboard_entry_project_id_fk", projectId, Project)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table DashboardEntry */
  lazy val DashboardEntry = new TableQuery(tag => new DashboardEntry(tag))

  /** Entity class storing rows of table LoginThrottle
   *  @param userId Database column user_id SqlType(uuid), PrimaryKey
   *  @param failedAttempts Database column failed_attempts SqlType(int4)
   *  @param lastAttemptAt Database column last_attempt_at SqlType(timestamp) */
  case class LoginThrottleRow(userId: java.util.UUID, failedAttempts: Int, lastAttemptAt: java.sql.Timestamp)
  /** GetResult implicit for fetching LoginThrottleRow objects using plain SQL queries */
  implicit def GetResultLoginThrottleRow(implicit e0: GR[java.util.UUID], e1: GR[Int], e2: GR[java.sql.Timestamp]): GR[LoginThrottleRow] = GR{
    prs => import prs._
    LoginThrottleRow.tupled((<<[java.util.UUID], <<[Int], <<[java.sql.Timestamp]))
  }
  /** Table description of table login_throttle. Objects of this class serve as prototypes for rows in queries. */
  class LoginThrottle(_tableTag: Tag) extends profile.api.Table[LoginThrottleRow](_tableTag, "login_throttle") {
    def * = (userId, failedAttempts, lastAttemptAt) <> (LoginThrottleRow.tupled, LoginThrottleRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(userId), Rep.Some(failedAttempts), Rep.Some(lastAttemptAt))).shaped.<>({r=>import r._; _1.map(_=> LoginThrottleRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(uuid), PrimaryKey */
    val userId: Rep[java.util.UUID] = column[java.util.UUID]("user_id", O.PrimaryKey)
    /** Database column failed_attempts SqlType(int4) */
    val failedAttempts: Rep[Int] = column[Int]("failed_attempts")
    /** Database column last_attempt_at SqlType(timestamp) */
    val lastAttemptAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("last_attempt_at")

    /** Foreign key referencing User (database name login_throttle_user_id_fk) */
    lazy val userFk = foreignKey("login_throttle_user_id_fk", userId, User)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table LoginThrottle */
  lazy val LoginThrottle = new TableQuery(tag => new LoginThrottle(tag))

  /** Entity class storing rows of table Project
   *  @param id Database column id SqlType(uuid), PrimaryKey
   *  @param ownerId Database column owner_id SqlType(uuid)
   *  @param name Database column name SqlType(text)
   *  @param description Database column description SqlType(text), Default(None)
   *  @param createdAt Database column created_at SqlType(timestamptz)
   *  @param updatedAt Database column updated_at SqlType(timestamptz), Default(None) */
  case class ProjectRow(id: java.util.UUID, ownerId: java.util.UUID, name: String, description: Option[String] = None, createdAt: java.sql.Timestamp, updatedAt: Option[java.sql.Timestamp] = None)
  /** GetResult implicit for fetching ProjectRow objects using plain SQL queries */
  implicit def GetResultProjectRow(implicit e0: GR[java.util.UUID], e1: GR[String], e2: GR[Option[String]], e3: GR[java.sql.Timestamp], e4: GR[Option[java.sql.Timestamp]]): GR[ProjectRow] = GR{
    prs => import prs._
    ProjectRow.tupled((<<[java.util.UUID], <<[java.util.UUID], <<[String], <<?[String], <<[java.sql.Timestamp], <<?[java.sql.Timestamp]))
  }
  /** Table description of table project. Objects of this class serve as prototypes for rows in queries. */
  class Project(_tableTag: Tag) extends profile.api.Table[ProjectRow](_tableTag, "project") {
    def * = (id, ownerId, name, description, createdAt, updatedAt) <> (ProjectRow.tupled, ProjectRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(ownerId), Rep.Some(name), description, Rep.Some(createdAt), updatedAt)).shaped.<>({r=>import r._; _1.map(_=> ProjectRow.tupled((_1.get, _2.get, _3.get, _4, _5.get, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)
    /** Database column owner_id SqlType(uuid) */
    val ownerId: Rep[java.util.UUID] = column[java.util.UUID]("owner_id")
    /** Database column name SqlType(text) */
    val name: Rep[String] = column[String]("name")
    /** Database column description SqlType(text), Default(None) */
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Default(None))
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")
    /** Database column updated_at SqlType(timestamptz), Default(None) */
    val updatedAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("updated_at", O.Default(None))

    /** Foreign key referencing User (database name project_owner_id_fk) */
    lazy val userFk = foreignKey("project_owner_id_fk", ownerId, User)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Project */
  lazy val Project = new TableQuery(tag => new Project(tag))

  /** Entity class storing rows of table Session
   *  @param id Database column id SqlType(uuid), PrimaryKey
   *  @param userId Database column user_id SqlType(uuid)
   *  @param createdAt Database column created_at SqlType(timestamptz) */
  case class SessionRow(id: java.util.UUID, userId: java.util.UUID, createdAt: java.sql.Timestamp)
  /** GetResult implicit for fetching SessionRow objects using plain SQL queries */
  implicit def GetResultSessionRow(implicit e0: GR[java.util.UUID], e1: GR[java.sql.Timestamp]): GR[SessionRow] = GR{
    prs => import prs._
    SessionRow.tupled((<<[java.util.UUID], <<[java.util.UUID], <<[java.sql.Timestamp]))
  }
  /** Table description of table session. Objects of this class serve as prototypes for rows in queries. */
  class Session(_tableTag: Tag) extends profile.api.Table[SessionRow](_tableTag, "session") {
    def * = (id, userId, createdAt) <> (SessionRow.tupled, SessionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(userId), Rep.Some(createdAt))).shaped.<>({r=>import r._; _1.map(_=> SessionRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)
    /** Database column user_id SqlType(uuid) */
    val userId: Rep[java.util.UUID] = column[java.util.UUID]("user_id")
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")

    /** Foreign key referencing User (database name session_user_id_fk) */
    lazy val userFk = foreignKey("session_user_id_fk", userId, User)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Session */
  lazy val Session = new TableQuery(tag => new Session(tag))

  /** Entity class storing rows of table Simulation
   *  @param taskId Database column task_id SqlType(uuid)
   *  @param projectId Database column project_id SqlType(uuid)
   *  @param dashboardId Database column dashboard_id SqlType(uuid)
   *  @param reachedModifier Database column reached_modifier SqlType(int4) */
  case class SimulationRow(taskId: java.util.UUID, projectId: java.util.UUID, dashboardId: java.util.UUID, reachedModifier: Int)
  /** GetResult implicit for fetching SimulationRow objects using plain SQL queries */
  implicit def GetResultSimulationRow(implicit e0: GR[java.util.UUID], e1: GR[Int]): GR[SimulationRow] = GR{
    prs => import prs._
    SimulationRow.tupled((<<[java.util.UUID], <<[java.util.UUID], <<[java.util.UUID], <<[Int]))
  }
  /** Table description of table simulation. Objects of this class serve as prototypes for rows in queries. */
  class Simulation(_tableTag: Tag) extends profile.api.Table[SimulationRow](_tableTag, "simulation") {
    def * = (taskId, projectId, dashboardId, reachedModifier) <> (SimulationRow.tupled, SimulationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(taskId), Rep.Some(projectId), Rep.Some(dashboardId), Rep.Some(reachedModifier))).shaped.<>({r=>import r._; _1.map(_=> SimulationRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column task_id SqlType(uuid) */
    val taskId: Rep[java.util.UUID] = column[java.util.UUID]("task_id")
    /** Database column project_id SqlType(uuid) */
    val projectId: Rep[java.util.UUID] = column[java.util.UUID]("project_id")
    /** Database column dashboard_id SqlType(uuid) */
    val dashboardId: Rep[java.util.UUID] = column[java.util.UUID]("dashboard_id")
    /** Database column reached_modifier SqlType(int4) */
    val reachedModifier: Rep[Int] = column[Int]("reached_modifier")

    /** Primary key of Simulation (database name simulation_pk) */
    val pk = primaryKey("simulation_pk", (taskId, dashboardId))

    /** Foreign key referencing Dashboard (database name simulation_dashboard_id_fk) */
    lazy val dashboardFk = foreignKey("simulation_dashboard_id_fk", dashboardId, Dashboard)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing Project (database name simulation_project_id_fk) */
    lazy val projectFk = foreignKey("simulation_project_id_fk", projectId, Project)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing Task (database name simulation_task_id_fk) */
    lazy val taskFk = foreignKey("simulation_task_id_fk", (taskId, projectId), Task)(r => (r.id, r.projectId), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Simulation */
  lazy val Simulation = new TableQuery(tag => new Simulation(tag))

  /** Entity class storing rows of table Task
   *  @param id Database column id SqlType(uuid)
   *  @param projectId Database column project_id SqlType(uuid)
   *  @param name Database column name SqlType(text)
   *  @param unit Database column unit SqlType(text), Default(None)
   *  @param kind Database column kind SqlType(text)
   *  @param reached Database column reached SqlType(numeric)
   *  @param reachable Database column reachable SqlType(numeric)
   *  @param counting Database column counting SqlType(bool)
   *  @param createdAt Database column created_at SqlType(timestamptz)
   *  @param updatedAt Database column updated_at SqlType(timestamptz), Default(None) */
  case class TaskRow(id: java.util.UUID, projectId: java.util.UUID, name: String, unit: Option[String] = None, kind: String, reached: scala.math.BigDecimal, reachable: scala.math.BigDecimal, counting: Boolean, createdAt: java.sql.Timestamp, updatedAt: Option[java.sql.Timestamp] = None)
  /** GetResult implicit for fetching TaskRow objects using plain SQL queries */
  implicit def GetResultTaskRow(implicit e0: GR[java.util.UUID], e1: GR[String], e2: GR[Option[String]], e3: GR[scala.math.BigDecimal], e4: GR[Boolean], e5: GR[java.sql.Timestamp], e6: GR[Option[java.sql.Timestamp]]): GR[TaskRow] = GR{
    prs => import prs._
    TaskRow.tupled((<<[java.util.UUID], <<[java.util.UUID], <<[String], <<?[String], <<[String], <<[scala.math.BigDecimal], <<[scala.math.BigDecimal], <<[Boolean], <<[java.sql.Timestamp], <<?[java.sql.Timestamp]))
  }
  /** Table description of table task. Objects of this class serve as prototypes for rows in queries. */
  class Task(_tableTag: Tag) extends profile.api.Table[TaskRow](_tableTag, "task") {
    def * = (id, projectId, name, unit, kind, reached, reachable, counting, createdAt, updatedAt) <> (TaskRow.tupled, TaskRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(projectId), Rep.Some(name), unit, Rep.Some(kind), Rep.Some(reached), Rep.Some(reachable), Rep.Some(counting), Rep.Some(createdAt), updatedAt)).shaped.<>({r=>import r._; _1.map(_=> TaskRow.tupled((_1.get, _2.get, _3.get, _4, _5.get, _6.get, _7.get, _8.get, _9.get, _10)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(uuid) */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id")
    /** Database column project_id SqlType(uuid) */
    val projectId: Rep[java.util.UUID] = column[java.util.UUID]("project_id")
    /** Database column name SqlType(text) */
    val name: Rep[String] = column[String]("name")
    /** Database column unit SqlType(text), Default(None) */
    val unit: Rep[Option[String]] = column[Option[String]]("unit", O.Default(None))
    /** Database column kind SqlType(text) */
    val kind: Rep[String] = column[String]("kind")
    /** Database column reached SqlType(numeric) */
    val reached: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("reached")
    /** Database column reachable SqlType(numeric) */
    val reachable: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("reachable")
    /** Database column counting SqlType(bool) */
    val counting: Rep[Boolean] = column[Boolean]("counting")
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")
    /** Database column updated_at SqlType(timestamptz), Default(None) */
    val updatedAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("updated_at", O.Default(None))

    /** Primary key of Task (database name task_pk) */
    val pk = primaryKey("task_pk", (id, projectId))

    /** Foreign key referencing Project (database name task_project_id) */
    lazy val projectFk = foreignKey("task_project_id", projectId, Project)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Task */
  lazy val Task = new TableQuery(tag => new Task(tag))

  /** Entity class storing rows of table User
   *  @param id Database column id SqlType(uuid), PrimaryKey
   *  @param nickname Database column nickname SqlType(text)
   *  @param email Database column email SqlType(text)
   *  @param salt Database column salt SqlType(text)
   *  @param hash Database column hash SqlType(text)
   *  @param displayName Database column display_name SqlType(text), Default(None) */
  case class UserRow(id: java.util.UUID, nickname: String, email: String, salt: String, hash: String, displayName: Option[String] = None)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[java.util.UUID], e1: GR[String], e2: GR[Option[String]]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[java.util.UUID], <<[String], <<[String], <<[String], <<[String], <<?[String]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(_tableTag: Tag) extends profile.api.Table[UserRow](_tableTag, "user") {
    def * = (id, nickname, email, salt, hash, displayName) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(nickname), Rep.Some(email), Rep.Some(salt), Rep.Some(hash), displayName)).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)
    /** Database column nickname SqlType(text) */
    val nickname: Rep[String] = column[String]("nickname")
    /** Database column email SqlType(text) */
    val email: Rep[String] = column[String]("email")
    /** Database column salt SqlType(text) */
    val salt: Rep[String] = column[String]("salt")
    /** Database column hash SqlType(text) */
    val hash: Rep[String] = column[String]("hash")
    /** Database column display_name SqlType(text), Default(None) */
    val displayName: Rep[Option[String]] = column[Option[String]]("display_name", O.Default(None))

    /** Uniqueness Index over (nickname) (database name user_nickname_unique) */
    val index1 = index("user_nickname_unique", nickname, unique=true)
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))
}
