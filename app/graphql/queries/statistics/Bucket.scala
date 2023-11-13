package graphql.queries.statistics

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveEnumType
import sangria.schema.EnumType

sealed trait Bucket extends EnumEntry

object Bucket extends Enum[Bucket] with CirceEnum[Bucket] {
  override lazy val values: IndexedSeq[Bucket] = findValues

  case object Below10 extends Bucket
  case object Below20 extends Bucket

  case object Below30 extends Bucket

  case object Below40 extends Bucket
  case object Below50 extends Bucket
  case object Below60 extends Bucket
  case object Below70 extends Bucket
  case object Below80 extends Bucket
  case object Below90 extends Bucket

  case object Below100 extends Bucket

  case object Exactly100 extends Bucket

  implicit val objectType: EnumType[Bucket] = deriveEnumType[Bucket]()

  implicit val fromInternal: Transformer[processing.statistics.dashboard.Bucket, Bucket] = {
    case processing.statistics.dashboard.Bucket.Below10    => Below10
    case processing.statistics.dashboard.Bucket.Below20    => Below20
    case processing.statistics.dashboard.Bucket.Below30    => Below30
    case processing.statistics.dashboard.Bucket.Below40    => Below40
    case processing.statistics.dashboard.Bucket.Below50    => Below50
    case processing.statistics.dashboard.Bucket.Below60    => Below60
    case processing.statistics.dashboard.Bucket.Below70    => Below70
    case processing.statistics.dashboard.Bucket.Below80    => Below80
    case processing.statistics.dashboard.Bucket.Below90    => Below90
    case processing.statistics.dashboard.Bucket.Below100   => Below100
    case processing.statistics.dashboard.Bucket.Exactly100 => Exactly100
  }

}
