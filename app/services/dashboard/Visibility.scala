package services.dashboard

import enumeratum.EnumEntry
import enumeratum.Enum

sealed trait Visibility extends EnumEntry

object Visibility extends Enum[Visibility] {
  case object Public  extends Visibility
  case object Private extends Visibility

  override lazy val values: IndexedSeq[Visibility] = findValues
}
