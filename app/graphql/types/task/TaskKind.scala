package graphql.types.task

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import io.circe.Json
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveEnumType
import sangria.marshalling.ToInput
import sangria.marshalling.circe.circeEncoderToInput
import sangria.schema.EnumType

sealed trait TaskKind extends EnumEntry

object TaskKind extends Enum[TaskKind] with CirceEnum[TaskKind] {
  case object Discrete extends TaskKind
  case object Percent  extends TaskKind
  case object Fraction extends TaskKind

  override lazy val values: IndexedSeq[TaskKind] = findValues

  implicit val fromInternal: Transformer[services.task.TaskKind, TaskKind] = {
    case services.task.TaskKind.Discrete => Discrete
    case services.task.TaskKind.Percent  => Percent
    case services.task.TaskKind.Fraction => Fraction
  }

  implicit val toInternal: Transformer[TaskKind, services.task.TaskKind] = {
    case Discrete => services.task.TaskKind.Discrete
    case Percent  => services.task.TaskKind.Percent
    case Fraction => services.task.TaskKind.Fraction
  }

  implicit val taskKindToInput: ToInput[TaskKind, Json] = circeEncoderToInput[TaskKind]

  implicit val taskKindObjectType: EnumType[TaskKind] = deriveEnumType[TaskKind]()
}
