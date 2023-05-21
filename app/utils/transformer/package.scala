package utils

import io.scalaland.chimney.Transformer
import shapeless.tag
import shapeless.tag.@@

import java.time.{ LocalDate, LocalDateTime, LocalTime }

package object transformer {

  object implicits {
    implicit def fromUntagged[A, Tag]: Transformer[A, A @@ Tag] = tag[Tag](_)

    implicit def toUntagged[A, Tag]: Transformer[A @@ Tag, A] = id => id: A

    implicit val localDateTimeToSqlTimestamp: Transformer[LocalDateTime, java.sql.Timestamp] =
      java.sql.Timestamp.valueOf

    implicit val sqlTimestampToLocalDateTime: Transformer[java.sql.Timestamp, LocalDateTime] =
      _.toLocalDateTime

  }

}
