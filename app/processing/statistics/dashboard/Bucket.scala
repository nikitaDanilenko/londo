package processing.statistics.dashboard

import enumeratum.EnumEntry
import enumeratum.Enum

sealed trait Bucket extends EnumEntry

object Bucket extends Enum[Bucket] {
  override lazy val values: IndexedSeq[Bucket] = findValues

  case object B10 extends Bucket
  case object B20 extends Bucket

  case object B30 extends Bucket

  case object B40 extends Bucket
  case object B50 extends Bucket
  case object B60 extends Bucket
  case object B70 extends Bucket
  case object B80 extends Bucket
  case object B90 extends Bucket

}
