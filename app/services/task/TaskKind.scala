package services.task

import enumeratum.{ Enum, EnumEntry }

sealed trait TaskKind extends EnumEntry

object TaskKind extends Enum[TaskKind] {
  case object Discrete extends TaskKind
  case object Percent  extends TaskKind
  case object Fraction extends TaskKind

  override lazy val values: IndexedSeq[TaskKind] = findValues

}
