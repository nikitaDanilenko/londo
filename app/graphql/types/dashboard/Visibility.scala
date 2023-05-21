package graphql.types.dashboard

import enumeratum.EnumEntry
import enumeratum.Enum
import io.circe.{ Codec, Json }
import io.circe.generic.extras.semiauto.deriveEnumerationCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveEnumType
import sangria.marshalling.ToInput
import sangria.marshalling.circe.circeEncoderToInput
import sangria.schema.EnumType

sealed trait Visibility extends EnumEntry

object Visibility extends Enum[Visibility] {
  case object Public  extends Visibility
  case object Private extends Visibility

  override lazy val values: IndexedSeq[Visibility] = findValues

  implicit val toInternal: Transformer[Visibility, services.dashboard.Visibility] = {
    case Public  => services.dashboard.Visibility.Public
    case Private => services.dashboard.Visibility.Private
  }

  implicit val fromInternal: Transformer[services.dashboard.Visibility, Visibility] = {
    case services.dashboard.Visibility.Public  => Public
    case services.dashboard.Visibility.Private => Private
  }

  implicit val visibilityCodec: Codec[Visibility] = deriveEnumerationCodec[Visibility]

  implicit val visibilityToInput: ToInput[Visibility, Json] = circeEncoderToInput[Visibility]

  implicit val visibilityObjectType: EnumType[Visibility] = deriveEnumType[Visibility]()

}
