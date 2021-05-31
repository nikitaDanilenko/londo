package graphql.types.task

import enumeratum.{ Enum, EnumEntry }
import io.circe.{ Codec, Json }
import io.circe.generic.extras.semiauto.deriveEnumerationCodec
import sangria.marshalling.ToInput
import sangria.marshalling.circe.circeEncoderToInput
import sangria.schema.{ EnumType, ObjectType }
import sangria.macros.derive.{ deriveEnumType, deriveObjectType }

sealed trait TaskKind extends EnumEntry

object TaskKind extends Enum[TaskKind] {
  case object Discrete extends TaskKind
  case object Percentual extends TaskKind
  case object Fractional extends TaskKind

  override lazy val values: IndexedSeq[TaskKind] = findValues

  def fromInternal(taskKind: services.task.TaskKind): TaskKind =
    taskKind match {
      case services.task.TaskKind.Discrete   => Discrete
      case services.task.TaskKind.Percentual => Percentual
      case services.task.TaskKind.Fractional => Fractional
    }

  def toInternal(taskKind: TaskKind): services.task.TaskKind =
    taskKind match {
      case Discrete   => services.task.TaskKind.Discrete
      case Percentual => services.task.TaskKind.Percentual
      case Fractional => services.task.TaskKind.Fractional
    }

  implicit val taskKindCodec: Codec[TaskKind] = deriveEnumerationCodec[TaskKind]

  implicit val taskKindToInput: ToInput[TaskKind, Json] = circeEncoderToInput[TaskKind]

  implicit val taskKindObjectType: EnumType[TaskKind] = deriveEnumType[TaskKind]()
}
