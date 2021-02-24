package db

import db.models._

trait PublicExtensions[Idiom <: io.getquill.idiom.Idiom, Naming <: io.getquill.NamingStrategy] {
  this: io.getquill.context.Context[Idiom, Naming] =>

  object PublicSchema {

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
