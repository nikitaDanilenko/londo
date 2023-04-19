package utils

import io.scalaland.chimney.Transformer
import shapeless.tag
import shapeless.tag.@@

import java.time.{ LocalDate, LocalTime }

package object transformer {

  object implicits {
    implicit def fromUntagged[A, Tag]: Transformer[A, A @@ Tag] = tag[Tag](_)

    implicit def toUntagged[A, Tag]: Transformer[A @@ Tag, A] = id => id: A

    // TODO: Check necessity
    implicit val localDateToSqlDate: Transformer[LocalDate, java.sql.Date] =
      java.sql.Date.valueOf

    implicit val sqlDateToLocalDate: Transformer[java.sql.Date, LocalDate] =
      _.toLocalDate

    implicit val localTimeToSqlTime: Transformer[LocalTime, java.sql.Time] =
      java.sql.Time.valueOf

    implicit val sqlTimeToLocalTime: Transformer[java.sql.Time, LocalTime] =
      _.toLocalTime

  }

}
