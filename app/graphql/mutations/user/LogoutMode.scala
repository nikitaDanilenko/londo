package graphql.mutations.user

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import io.circe.Json
import sangria.macros.derive.deriveEnumType
import sangria.marshalling.ToInput
import sangria.marshalling.circe.circeEncoderToInput
import sangria.schema.EnumType

sealed trait LogoutMode extends EnumEntry

object LogoutMode extends Enum[LogoutMode] with CirceEnum[LogoutMode] {
  case object ThisSession extends LogoutMode
  case object AllSessions extends LogoutMode

  override lazy val values: IndexedSeq[LogoutMode] = findValues

  implicit val toInput: ToInput[LogoutMode, Json] = circeEncoderToInput[LogoutMode]

  implicit val enumType: EnumType[LogoutMode] = deriveEnumType[LogoutMode]()
}
