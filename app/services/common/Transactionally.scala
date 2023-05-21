package services.common

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

object Transactionally {

  object syntax {

    implicit class WithRunTransactionally(private val databaseDef: PostgresProfile#Backend#Database) extends AnyVal {

      def runTransactionally[A](dbio: DBIO[A]): Future[A] =
        databaseDef.run(dbio.transactionally)

    }

  }

}
