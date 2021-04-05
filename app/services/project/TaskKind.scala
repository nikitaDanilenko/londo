package services.project

import enumeratum.{ EnumEntry, Enum }

import java.util.UUID

sealed trait TaskKind extends EnumEntry

object TaskKind extends Enum[TaskKind] {
  case object Discrete extends TaskKind
  case object Percentual extends TaskKind
  case object Fractional extends TaskKind

  override lazy val values: IndexedSeq[TaskKind] = findValues

  // TODO: Add a validation step so that the map is always in sync with the database values
  private val taskKindMap: Map[UUID, TaskKind] = Map(
    UUID.fromString("005c8772-56b6-4ebb-afa6-e27d1d987f86") -> Discrete,
    UUID.fromString("7c1917d4-2743-4419-856e-c7a3b6ef540e") -> Percentual,
    UUID.fromString("1cb2c09f-cdbd-4318-9dad-fcee1b16c0d4") -> Fractional
  )

  private val reverseMap: Map[TaskKind, UUID] = taskKindMap.map(_.swap)

  def fromRow(taskKindRow: db.models.TaskKind): TaskKind =
    taskKindMap.getOrElse(taskKindRow.id, Fractional)

  def toRow(taskKind: TaskKind): db.models.TaskKind =
    db.models.TaskKind(
      id = reverseMap.getOrElse(taskKind, reverseMap(Fractional)),
      name = taskKind.entryName
    )

}
