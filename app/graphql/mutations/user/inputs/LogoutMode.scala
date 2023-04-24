package graphql.mutations.user.inputs

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import sangria.macros.derive.deriveEnumType
import sangria.schema.EnumType

sealed trait LogoutMode extends EnumEntry

object LogoutMode extends Enum[LogoutMode] with CirceEnum[LogoutMode] {
  case object ThisSession extends LogoutMode
  case object AllSessions extends LogoutMode

  override lazy val values: IndexedSeq[LogoutMode] = findValues

  implicit val enumType: EnumType[LogoutMode] = deriveEnumType[LogoutMode]()
}
