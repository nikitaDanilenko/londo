package services.task

import enumeratum.{Enum, EnumEntry}

sealed trait TaskKind extends EnumEntry

object TaskKind extends Enum[TaskKind] {
  case object Discrete   extends TaskKind
  case object Percentual extends TaskKind
  case object Fractional extends TaskKind

  override lazy val values: IndexedSeq[TaskKind] = findValues

}
