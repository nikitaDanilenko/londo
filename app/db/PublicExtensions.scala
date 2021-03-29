package db

import db.models._

trait PublicExtensions[Idiom <: io.getquill.idiom.Idiom, Naming <: io.getquill.NamingStrategy] {
  this: io.getquill.context.Context[Idiom, Naming] =>

  object PublicSchema {

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

    object TaskKindDao {

      def query =
        quote {
          querySchema[TaskKind](
            "public.task_kind"
          )

        }

    }

    object ProjectAccessDao {

      def query =
        quote {
          querySchema[ProjectAccess](
            "public.project_access"
          )

        }

    }

    object DashboardRestrictionDao {

      def query =
        quote {
          querySchema[DashboardRestriction](
            "public.dashboard_restriction"
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

    object DashboardRestrictionAccessDao {

      def query =
        quote {
          querySchema[DashboardRestrictionAccess](
            "public.dashboard_restriction_access"
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

    object TaskDao {

      def query =
        quote {
          querySchema[Task](
            "public.task"
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

  }

}
