package graphql.mutations.user

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import io.circe.generic.JsonCodec
import io.circe.{ Decoder, Encoder }

import java.util.UUID

@JsonCodec
case class UserOperation[O](
    userId: UUID,
    operation: O
)

object UserOperation {

  sealed trait Operation extends EnumEntry

  object Operation extends Enum[Operation] with CirceEnum[Operation] {

    case object Recovery extends Operation {
      implicit val decoder: Decoder[Recovery] = Decoder[Operation].map(_ => Recovery)
      implicit val encoder: Encoder[Recovery] = Encoder[Operation].contramap(x => x: Operation)
    }

    type Recovery = Recovery.type

    case object Deletion extends Operation {
      implicit val decoder: Decoder[Deletion] = Decoder[Operation].map(_ => Deletion)
      implicit val encoder: Encoder[Deletion] = Encoder[Operation].contramap(x => x: Operation)
    }

    type Deletion = Deletion.type

    override lazy val values: IndexedSeq[Operation] = findValues
  }

}
