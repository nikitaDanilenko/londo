package services.user

import db.{ DbContext, models }
import io.getquill.EntityQuery

import javax.inject.Inject

class UserDTO @Inject() (val dbContext: DbContext) { self =>

  import dbContext._

//  def create(userCreation: UserCreation)(implicit contextShift: ContextShift[IO]): IO[User] = {
//    UserCreation
//      .create(userCreation)
//      .flatMap { user =>
//        run(insertAction(user)).transact(transactor)
//      }
//  }
//

//  val dao: MacroDAO[models.User] = new MacroDAO[models.User] {
//    override val dbContext: DbContext = self.dbContext
//  }

//  def insert[F[_]: Async](
//      row: models.User
//  )(implicit contextShift: ContextShift[F]): F[dao.dbContext.Quoted[ActionReturning[models.User, models.User]]] =
//    run(dao.insertAction(row)).transact(transactor[F])

//  def insertAction(user: User) =
//    quote {
//      PublicSchema.UserDao.query
//        .insert(lift(UserConverter.toRow(user)))
//        .returning(x => x)
//    }
//
//  def insertAllAction(rows: Seq[models.User]) =
//    quote {
//      liftQuery(rows).foreach(query[models.User].insert(_))
//    }
//
//  def insertAll(rows: Seq[models.User])(implicit contextShift: ContextShift[IO]) =
//    run(insertAllAction(rows)).transact(transactor)

//  def insert(user: User)(implicit contextShift: ContextShift[IO]): IO[User] =
//    run(insertAction(user)).transact(transactor).map(UserConverter.fromRow)

//  def insertAllAction(users: Seq[User]): BatchAction[ActionReturning[models.User, User]] =
//    liftQuery(users).foreach(insertAction)
//
//  def insertAll(users: Seq[User])(implicit contextShift: ContextShift[IO]): IO[List[User]] =
//    run(insertAllAction(users)).transact(transactor)
//
//  def findAction(userId: UserId): EntityQuery[User] =
//    PublicSchema.UserDao.query.filter(_.id == userId.uuid).map(UserConverter.fromRow)
//
//  def find(userId: UserId)(implicit contextShift: ContextShift[IO]): IO[Option[User]] =
//    run(findAction(userId)).transact(transactor).map(_.headOption)
//
//  def findAllAction(userIds: Seq[UserId]): EntityQuery[User] = {
//    val idSet = userIds.map(_.uuid).toSet
//    PublicSchema.UserDao.query.filter(user => idSet.contains(user.id)).map(UserConverter.fromRow)
//  }
//
//  def findAll(userIds: Seq[UserId])(implicit contextShift: ContextShift[IO]): IO[List[User]] =
//    run(findAllAction(userIds)).transact(transactor)

  def q: dbContext.Quoted[EntityQuery[models.User]] = PublicSchema.UserDao.query

//  val q2: EntityQuery[User] = query[User]
//  val dao2 = new DAO.Instance[models.User, User, UserId](
//    UserConverter.fromRow,
//    UserConverter.toRow,
//    u => UserId(u.id),
//    ""
//  ) {
//    override val dbContext: DbContext = self.dbContext
//    override def schema: Quoted[EntityQuery[models.User]] = dbContext.PublicSchema.UserDao.query
//  }

//  val dao = new DAO[models.User, User, UserId] {
//    override val dbContext: DbContext = self.dbContext
//    override def schema: dbContext.Quoted[EntityQuery[models.User]] = dbContext.PublicSchema.UserDao.query
//    override val fromRow: models.User => User = UserConverter.fromRow
//    override val toRow: User => models.User = UserConverter.toRow
//    override val keyOf: models.User => UserId = u => UserId(u.id)
//    override def schemaName: String = "user"
//  }

}
