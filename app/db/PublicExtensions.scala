package db

import db.models._

trait PublicExtensions[Idiom <: io.getquill.idiom.Idiom, Naming <: io.getquill.NamingStrategy] {
  this: io.getquill.context.Context[Idiom, Naming] =>

  object PublicSchema {

    object DashboardReadAccessEntryDao {

      def query =
        quote {
          querySchema[DashboardReadAccessEntry](
            "public.dashboard_read_access_entry"
          )

        }

    }

    object DashboardReadAccessDao {

      def query =
        quote {
          querySchema[DashboardReadAccess](
            "public.dashboard_read_access"
          )

        }

    }

    object ProjectWriteAccessDao {

      def query =
        quote {
          querySchema[ProjectWriteAccess](
            "public.project_write_access"
          )

        }

    }

    object RegistrationTokenDao {

      def query =
        quote {
          querySchema[RegistrationToken](
            "public.registration_token"
          )

        }

    }

    object UserDao {

      def query =
        quote {
          querySchema[User](
            "public.user"
          )

        }

    }

    object ProjectReadAccessEntryDao {

      def query =
        quote {
          querySchema[ProjectReadAccessEntry](
            "public.project_read_access_entry"
          )

        }

    }

    object TaskKindDao {

      def query =
        quote {
          querySchema[TaskKind](
            "public.task_kind"
          )

        }

    }

    object DashboardWriteAccessDao {

      def query =
        quote {
          querySchema[DashboardWriteAccess](
            "public.dashboard_write_access"
          )

        }

    }

    object LoginAttemptDao {

      def query =
        quote {
          querySchema[LoginAttempt](
            "public.login_attempt"
          )

        }

    }

    object UserSettingsDao {

      def query =
        quote {
          querySchema[UserSettings](
            "public.user_settings"
          )

        }

    }

    object ProjectWriteAccessEntryDao {

      def query =
        quote {
          querySchema[ProjectWriteAccessEntry](
            "public.project_write_access_entry"
          )

        }

    }

    object ProjectReadAccessDao {

      def query =
        quote {
          querySchema[ProjectReadAccess](
            "public.project_read_access"
          )

        }

    }

    object DashboardWriteAccessEntryDao {

      def query =
        quote {
          querySchema[DashboardWriteAccessEntry](
            "public.dashboard_write_access_entry"
          )

        }

    }

    object SessionKeyDao {

      def query =
        quote {
          querySchema[SessionKey](
            "public.session_key"
          )

        }

    }

    object UserDetailsDao {

      def query =
        quote {
          querySchema[UserDetails](
            "public.user_details"
          )

        }

    }

    object ProjectDao {

      def query =
        quote {
          querySchema[Project](
            "public.project"
          )

        }

    }

    object DashboardDao {

      def query =
        quote {
          querySchema[Dashboard](
            "public.dashboard"
          )

        }

    }

    object TaskDao {

      def query =
        quote {
          querySchema[Task](
            "public.task"
          )

        }

    }

    object DashboardProjectAssociationDao {

      def query =
        quote {
          querySchema[DashboardProjectAssociation](
            "public.dashboard_project_association"
          )

        }

    }

  }

}
