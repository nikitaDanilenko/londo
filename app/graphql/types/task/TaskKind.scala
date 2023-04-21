package graphql.types.task

import enumeratum.{ Enum, EnumEntry }
import io.circe.generic.extras.semiauto.deriveEnumerationCodec
import io.circe.{ Codec, Json }
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveEnumType
import sangria.marshalling.ToInput
import sangria.marshalling.circe.circeEncoderToInput
import sangria.schema.EnumType

sealed trait TaskKind extends EnumEntry

object TaskKind extends Enum[TaskKind] {
  case object Discrete   extends TaskKind
  case object Percentual extends TaskKind
  case object Fractional extends TaskKind

  override lazy val values: IndexedSeq[TaskKind] = findValues

  implicit val fromInternal: Transformer[services.task.TaskKind, TaskKind] = {
    case services.task.TaskKind.Discrete   => Discrete
    case services.task.TaskKind.Percentual => Percentual
    case services.task.TaskKind.Fractional => Fractional
  }

  implicit val toInternal: Transformer[TaskKind, services.task.TaskKind] = {
    case Discrete   => services.task.TaskKind.Discrete
    case Percentual => services.task.TaskKind.Percentual
    case Fractional => services.task.TaskKind.Fractional
  }

  implicit val taskKindCodec: Codec[TaskKind] = deriveEnumerationCodec[TaskKind]

  implicit val taskKindToInput: ToInput[TaskKind, Json] = circeEncoderToInput[TaskKind]

  implicit val taskKindObjectType: EnumType[TaskKind] = deriveEnumType[TaskKind]()
}
