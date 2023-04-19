package db.generated

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables
    extends {
      val profile = slick.jdbc.PostgresProfile
    }
    with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this
  * late.)
  */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(
    Dashboard.schema,
    DashboardEntry.schema,
    LoginAttempt.schema,
    PlainTask.schema,
    Project.schema,
    ProjectReferenceTask.schema,
    Session.schema,
    User.schema
  ).reduceLeft(_ ++ _)

  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Dashboard
    * @param id
    *   Database column id SqlType(uuid), PrimaryKey
    * @param userId
    *   Database column user_id SqlType(uuid)
    * @param header
    *   Database column header SqlType(text)
    * @param description
    *   Database column description SqlType(text), Default(None)
    * @param publiclyVisible
    *   Database column publicly_visible SqlType(bool)
    * @param createdAt
    *   Database column created_at SqlType(date)
    * @param updatedAt
    *   Database column updated_at SqlType(date), Default(None)
    */
  case class DashboardRow(
      id: java.util.UUID,
      userId: java.util.UUID,
      header: String,
      description: Option[String] = None,
      publiclyVisible: Boolean,
      createdAt: java.sql.Date,
      updatedAt: Option[java.sql.Date] = None
  )

  /** GetResult implicit for fetching DashboardRow objects using plain SQL queries */
  implicit def GetResultDashboardRow(implicit
      e0: GR[java.util.UUID],
      e1: GR[String],
      e2: GR[Option[String]],
      e3: GR[Boolean],
      e4: GR[java.sql.Date],
      e5: GR[Option[java.sql.Date]]
  ): GR[DashboardRow] = GR { prs =>
    import prs._
    DashboardRow.tupled(
      (
        <<[java.util.UUID],
        <<[java.util.UUID],
        <<[String],
        <<?[String],
        <<[Boolean],
        <<[java.sql.Date],
        <<?[java.sql.Date]
      )
    )
  }

  /** Table description of table dashboard. Objects of this class serve as prototypes for rows in queries. */
  class Dashboard(_tableTag: Tag) extends profile.api.Table[DashboardRow](_tableTag, "dashboard") {

    def * = (
      id,
      userId,
      header,
      description,
      publiclyVisible,
      createdAt,
      updatedAt
    ) <> (DashboardRow.tupled, DashboardRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (
      (
        Rep.Some(id),
        Rep.Some(userId),
        Rep.Some(header),
        description,
        Rep.Some(publiclyVisible),
        Rep.Some(createdAt),
        updatedAt
      )
    ).shaped.<>(
      { r => import r._; _1.map(_ => DashboardRow.tupled((_1.get, _2.get, _3.get, _4, _5.get, _6.get, _7))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)

    /** Database column user_id SqlType(uuid) */
    val userId: Rep[java.util.UUID] = column[java.util.UUID]("user_id")

    /** Database column header SqlType(text) */
    val header: Rep[String] = column[String]("header")

    /** Database column description SqlType(text), Default(None) */
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Default(None))

    /** Database column publicly_visible SqlType(bool) */
    val publiclyVisible: Rep[Boolean] = column[Boolean]("publicly_visible")

    /** Database column created_at SqlType(date) */
    val createdAt: Rep[java.sql.Date] = column[java.sql.Date]("created_at")

    /** Database column updated_at SqlType(date), Default(None) */
    val updatedAt: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("updated_at", O.Default(None))

    /** Foreign key referencing User (database name dashboard_user_id_fk) */
    lazy val userFk = foreignKey("dashboard_user_id_fk", userId, User)(
      r => r.id,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.NoAction
    )

  }

  /** Collection-like TableQuery object for table Dashboard */
  lazy val Dashboard = new TableQuery(tag => new Dashboard(tag))

  /** Entity class storing rows of table DashboardEntry
    * @param dashboardId
    *   Database column dashboard_id SqlType(uuid)
    * @param projectId
    *   Database column project_id SqlType(uuid)
    */
  case class DashboardEntryRow(dashboardId: java.util.UUID, projectId: java.util.UUID)

  /** GetResult implicit for fetching DashboardEntryRow objects using plain SQL queries */
  implicit def GetResultDashboardEntryRow(implicit
      e0: GR[java.util.UUID]
  ): GR[DashboardEntryRow] = GR { prs =>
    import prs._
    DashboardEntryRow.tupled((<<[java.util.UUID], <<[java.util.UUID]))
  }

  /** Table description of table dashboard_entry. Objects of this class serve as prototypes for rows in queries.
    */
  class DashboardEntry(_tableTag: Tag) extends profile.api.Table[DashboardEntryRow](_tableTag, "dashboard_entry") {
    def * = (dashboardId, projectId) <> (DashboardEntryRow.tupled, DashboardEntryRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(dashboardId), Rep.Some(projectId))).shaped.<>(
      { r => import r._; _1.map(_ => DashboardEntryRow.tupled((_1.get, _2.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column dashboard_id SqlType(uuid) */
    val dashboardId: Rep[java.util.UUID] = column[java.util.UUID]("dashboard_id")

    /** Database column project_id SqlType(uuid) */
    val projectId: Rep[java.util.UUID] = column[java.util.UUID]("project_id")

    /** Primary key of DashboardEntry (database name dashboard_entry_pk) */
    val pk = primaryKey("dashboard_entry_pk", (dashboardId, projectId))

    /** Foreign key referencing Dashboard (database name dashboard_entry_dashboard_id_fk) */
    lazy val dashboardFk = foreignKey("dashboard_entry_dashboard_id_fk", dashboardId, Dashboard)(
      r => r.id,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.Cascade
    )

    /** Foreign key referencing Project (database name dashboard_entry_project_id_fk) */
    lazy val projectFk = foreignKey("dashboard_entry_project_id_fk", projectId, Project)(
      r => r.id,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.Cascade
    )

  }

  /** Collection-like TableQuery object for table DashboardEntry */
  lazy val DashboardEntry = new TableQuery(tag => new DashboardEntry(tag))

  /** Entity class storing rows of table LoginAttempt
    * @param userId
    *   Database column user_id SqlType(uuid), PrimaryKey
    * @param failedAttemptsSinceLastSuccessfulLogin
    *   Database column failed_attempts_since_last_successful_login SqlType(int4)
    * @param lastSuccessfulLogin
    *   Database column last_successful_login SqlType(timestamp), Default(None)
    */
  case class LoginAttemptRow(
      userId: java.util.UUID,
      failedAttemptsSinceLastSuccessfulLogin: Int,
      lastSuccessfulLogin: Option[java.sql.Timestamp] = None
  )

  /** GetResult implicit for fetching LoginAttemptRow objects using plain SQL queries */
  implicit def GetResultLoginAttemptRow(implicit
      e0: GR[java.util.UUID],
      e1: GR[Int],
      e2: GR[Option[java.sql.Timestamp]]
  ): GR[LoginAttemptRow] = GR { prs =>
    import prs._
    LoginAttemptRow.tupled((<<[java.util.UUID], <<[Int], <<?[java.sql.Timestamp]))
  }

  /** Table description of table login_attempt. Objects of this class serve as prototypes for rows in queries. */
  class LoginAttempt(_tableTag: Tag) extends profile.api.Table[LoginAttemptRow](_tableTag, "login_attempt") {

    def * = (
      userId,
      failedAttemptsSinceLastSuccessfulLogin,
      lastSuccessfulLogin
    ) <> (LoginAttemptRow.tupled, LoginAttemptRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(userId), Rep.Some(failedAttemptsSinceLastSuccessfulLogin), lastSuccessfulLogin)).shaped.<>(
      { r => import r._; _1.map(_ => LoginAttemptRow.tupled((_1.get, _2.get, _3))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column user_id SqlType(uuid), PrimaryKey */
    val userId: Rep[java.util.UUID] = column[java.util.UUID]("user_id", O.PrimaryKey)

    /** Database column failed_attempts_since_last_successful_login SqlType(int4) */
    val failedAttemptsSinceLastSuccessfulLogin: Rep[Int] = column[Int]("failed_attempts_since_last_successful_login")

    /** Database column last_successful_login SqlType(timestamp), Default(None) */
    val lastSuccessfulLogin: Rep[Option[java.sql.Timestamp]] =
      column[Option[java.sql.Timestamp]]("last_successful_login", O.Default(None))

    /** Foreign key referencing User (database name login_attempt_user_id_fk) */
    lazy val userFk = foreignKey("login_attempt_user_id_fk", userId, User)(
      r => r.id,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.Cascade
    )

  }

  /** Collection-like TableQuery object for table LoginAttempt */
  lazy val LoginAttempt = new TableQuery(tag => new LoginAttempt(tag))

  /** Entity class storing rows of table PlainTask
    * @param id
    *   Database column id SqlType(uuid)
    * @param projectId
    *   Database column project_id SqlType(uuid)
    * @param name
    *   Database column name SqlType(text)
    * @param unit
    *   Database column unit SqlType(text), Default(None)
    * @param kind
    *   Database column kind SqlType(text)
    * @param reached
    *   Database column reached SqlType(int8)
    * @param reachable
    *   Database column reachable SqlType(int8)
    */
  case class PlainTaskRow(
      id: java.util.UUID,
      projectId: java.util.UUID,
      name: String,
      unit: Option[String] = None,
      kind: String,
      reached: Long,
      reachable: Long
  )

  /** GetResult implicit for fetching PlainTaskRow objects using plain SQL queries */
  implicit def GetResultPlainTaskRow(implicit
      e0: GR[java.util.UUID],
      e1: GR[String],
      e2: GR[Option[String]],
      e3: GR[Long]
  ): GR[PlainTaskRow] = GR { prs =>
    import prs._
    PlainTaskRow.tupled(
      (<<[java.util.UUID], <<[java.util.UUID], <<[String], <<?[String], <<[String], <<[Long], <<[Long])
    )
  }

  /** Table description of table plain_task. Objects of this class serve as prototypes for rows in queries. */
  class PlainTask(_tableTag: Tag) extends profile.api.Table[PlainTaskRow](_tableTag, "plain_task") {
    def * = (id, projectId, name, unit, kind, reached, reachable) <> (PlainTaskRow.tupled, PlainTaskRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (
      (
        Rep.Some(id),
        Rep.Some(projectId),
        Rep.Some(name),
        unit,
        Rep.Some(kind),
        Rep.Some(reached),
        Rep.Some(reachable)
      )
    ).shaped.<>(
      { r => import r._; _1.map(_ => PlainTaskRow.tupled((_1.get, _2.get, _3.get, _4, _5.get, _6.get, _7.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

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

    /** Database column reached SqlType(int8) */
    val reached: Rep[Long] = column[Long]("reached")

    /** Database column reachable SqlType(int8) */
    val reachable: Rep[Long] = column[Long]("reachable")

    /** Primary key of PlainTask (database name plain_task_pk) */
    val pk = primaryKey("plain_task_pk", (id, projectId))

    /** Foreign key referencing Project (database name plain_task_project_id) */
    lazy val projectFk = foreignKey("plain_task_project_id", projectId, Project)(
      r => r.id,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.Cascade
    )

  }

  /** Collection-like TableQuery object for table PlainTask */
  lazy val PlainTask = new TableQuery(tag => new PlainTask(tag))

  /** Entity class storing rows of table Project
    * @param id
    *   Database column id SqlType(uuid), PrimaryKey
    * @param ownerId
    *   Database column owner_id SqlType(uuid)
    * @param name
    *   Database column name SqlType(text)
    * @param description
    *   Database column description SqlType(text), Default(None)
    * @param createdAt
    *   Database column created_at SqlType(date)
    * @param updatedAt
    *   Database column updated_at SqlType(date), Default(None)
    */
  case class ProjectRow(
      id: java.util.UUID,
      ownerId: java.util.UUID,
      name: String,
      description: Option[String] = None,
      createdAt: java.sql.Date,
      updatedAt: Option[java.sql.Date] = None
  )

  /** GetResult implicit for fetching ProjectRow objects using plain SQL queries */
  implicit def GetResultProjectRow(implicit
      e0: GR[java.util.UUID],
      e1: GR[String],
      e2: GR[Option[String]],
      e3: GR[java.sql.Date],
      e4: GR[Option[java.sql.Date]]
  ): GR[ProjectRow] = GR { prs =>
    import prs._
    ProjectRow.tupled(
      (<<[java.util.UUID], <<[java.util.UUID], <<[String], <<?[String], <<[java.sql.Date], <<?[java.sql.Date])
    )
  }

  /** Table description of table project. Objects of this class serve as prototypes for rows in queries. */
  class Project(_tableTag: Tag) extends profile.api.Table[ProjectRow](_tableTag, "project") {
    def * = (id, ownerId, name, description, createdAt, updatedAt) <> (ProjectRow.tupled, ProjectRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(ownerId), Rep.Some(name), description, Rep.Some(createdAt), updatedAt)).shaped.<>(
      { r => import r._; _1.map(_ => ProjectRow.tupled((_1.get, _2.get, _3.get, _4, _5.get, _6))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)

    /** Database column owner_id SqlType(uuid) */
    val ownerId: Rep[java.util.UUID] = column[java.util.UUID]("owner_id")

    /** Database column name SqlType(text) */
    val name: Rep[String] = column[String]("name")

    /** Database column description SqlType(text), Default(None) */
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Default(None))

    /** Database column created_at SqlType(date) */
    val createdAt: Rep[java.sql.Date] = column[java.sql.Date]("created_at")

    /** Database column updated_at SqlType(date), Default(None) */
    val updatedAt: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("updated_at", O.Default(None))

    /** Foreign key referencing User (database name project_owner_id_fk) */
    lazy val userFk = foreignKey("project_owner_id_fk", ownerId, User)(
      r => r.id,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.Cascade
    )

  }

  /** Collection-like TableQuery object for table Project */
  lazy val Project = new TableQuery(tag => new Project(tag))

  /** Entity class storing rows of table ProjectReferenceTask
    * @param id
    *   Database column id SqlType(uuid)
    * @param projectId
    *   Database column project_id SqlType(uuid)
    * @param projectReferenceId
    *   Database column project_reference_id SqlType(uuid)
    */
  case class ProjectReferenceTaskRow(id: java.util.UUID, projectId: java.util.UUID, projectReferenceId: java.util.UUID)

  /** GetResult implicit for fetching ProjectReferenceTaskRow objects using plain SQL queries */
  implicit def GetResultProjectReferenceTaskRow(implicit e0: GR[java.util.UUID]): GR[ProjectReferenceTaskRow] = GR {
    prs =>
      import prs._
      ProjectReferenceTaskRow.tupled((<<[java.util.UUID], <<[java.util.UUID], <<[java.util.UUID]))
  }

  /** Table description of table project_reference_task. Objects of this class serve as prototypes for rows in queries.
    */
  class ProjectReferenceTask(_tableTag: Tag)
      extends profile.api.Table[ProjectReferenceTaskRow](_tableTag, "project_reference_task") {
    def * = (id, projectId, projectReferenceId) <> (ProjectReferenceTaskRow.tupled, ProjectReferenceTaskRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(projectId), Rep.Some(projectReferenceId))).shaped.<>(
      { r => import r._; _1.map(_ => ProjectReferenceTaskRow.tupled((_1.get, _2.get, _3.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(uuid) */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id")

    /** Database column project_id SqlType(uuid) */
    val projectId: Rep[java.util.UUID] = column[java.util.UUID]("project_id")

    /** Database column project_reference_id SqlType(uuid) */
    val projectReferenceId: Rep[java.util.UUID] = column[java.util.UUID]("project_reference_id")

    /** Primary key of ProjectReferenceTask (database name project_reference_task_pk) */
    val pk = primaryKey("project_reference_task_pk", (id, projectId))

    /** Foreign key referencing Project (database name project_reference_task_project_reference_id) */
    lazy val projectFk = foreignKey("project_reference_task_project_reference_id", projectId, Project)(
      r => r.id,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.Cascade
    )

  }

  /** Collection-like TableQuery object for table ProjectReferenceTask */
  lazy val ProjectReferenceTask = new TableQuery(tag => new ProjectReferenceTask(tag))

  /** Entity class storing rows of table Session
    * @param id
    *   Database column id SqlType(uuid), PrimaryKey
    * @param userId
    *   Database column user_id SqlType(uuid)
    * @param createdAt
    *   Database column created_at SqlType(date)
    */
  case class SessionRow(id: java.util.UUID, userId: java.util.UUID, createdAt: java.sql.Date)

  /** GetResult implicit for fetching SessionRow objects using plain SQL queries */
  implicit def GetResultSessionRow(implicit e0: GR[java.util.UUID], e1: GR[java.sql.Date]): GR[SessionRow] = GR { prs =>
    import prs._
    SessionRow.tupled((<<[java.util.UUID], <<[java.util.UUID], <<[java.sql.Date]))
  }

  /** Table description of table session. Objects of this class serve as prototypes for rows in queries. */
  class Session(_tableTag: Tag) extends profile.api.Table[SessionRow](_tableTag, "session") {
    def * = (id, userId, createdAt) <> (SessionRow.tupled, SessionRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(userId), Rep.Some(createdAt))).shaped.<>(
      { r => import r._; _1.map(_ => SessionRow.tupled((_1.get, _2.get, _3.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)

    /** Database column user_id SqlType(uuid) */
    val userId: Rep[java.util.UUID] = column[java.util.UUID]("user_id")

    /** Database column created_at SqlType(date) */
    val createdAt: Rep[java.sql.Date] = column[java.sql.Date]("created_at")

    /** Foreign key referencing User (database name session_user_id_fk) */
    lazy val userFk = foreignKey("session_user_id_fk", userId, User)(
      r => r.id,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.Cascade
    )

  }

  /** Collection-like TableQuery object for table Session */
  lazy val Session = new TableQuery(tag => new Session(tag))

  /** Entity class storing rows of table User
    * @param id
    *   Database column id SqlType(uuid), PrimaryKey
    * @param nickname
    *   Database column nickname SqlType(text)
    * @param email
    *   Database column email SqlType(text)
    * @param passwordSalt
    *   Database column password_salt SqlType(text)
    * @param passwordHash
    *   Database column password_hash SqlType(text)
    * @param displayName
    *   Database column display_name SqlType(text), Default(None)
    * @param description
    *   Database column description SqlType(text), Default(None)
    */
  case class UserRow(
      id: java.util.UUID,
      nickname: String,
      email: String,
      passwordSalt: String,
      passwordHash: String,
      displayName: Option[String] = None,
      description: Option[String] = None
  )

  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[java.util.UUID], e1: GR[String], e2: GR[Option[String]]): GR[UserRow] =
    GR { prs =>
      import prs._
      UserRow.tupled((<<[java.util.UUID], <<[String], <<[String], <<[String], <<[String], <<?[String], <<?[String]))
    }

  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(_tableTag: Tag) extends profile.api.Table[UserRow](_tableTag, "user") {

    def * =
      (id, nickname, email, passwordSalt, passwordHash, displayName, description) <> (UserRow.tupled, UserRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (
      (
        Rep.Some(id),
        Rep.Some(nickname),
        Rep.Some(email),
        Rep.Some(passwordSalt),
        Rep.Some(passwordHash),
        displayName,
        description
      )
    ).shaped.<>(
      { r => import r._; _1.map(_ => UserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6, _7))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)

    /** Database column nickname SqlType(text) */
    val nickname: Rep[String] = column[String]("nickname")

    /** Database column email SqlType(text) */
    val email: Rep[String] = column[String]("email")

    /** Database column password_salt SqlType(text) */
    val passwordSalt: Rep[String] = column[String]("password_salt")

    /** Database column password_hash SqlType(text) */
    val passwordHash: Rep[String] = column[String]("password_hash")

    /** Database column display_name SqlType(text), Default(None) */
    val displayName: Rep[Option[String]] = column[Option[String]]("display_name", O.Default(None))

    /** Database column description SqlType(text), Default(None) */
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Default(None))

    /** Uniqueness Index over (nickname) (database name user_nickname_unique) */
    val index1 = index("user_nickname_unique", nickname, unique = true)
  }

  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))
}
