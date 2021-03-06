package utils.json

import io.circe.{ Codec, Decoder, Encoder }
import spire.math.Natural

import scala.util.Try

object CirceUtil {

  trait Instances {

    implicit val naturalCodec: Codec[Natural] = {
      val encoder: Encoder[Natural] = Encoder[BigInt].contramap(_.toBigInt)
      val decoder: Decoder[Natural] =
        Decoder[BigInt].emap(bigInt => Try(Natural(bigInt)).toEither.left.map(_.getMessage))
      Codec.from(decoder, encoder)
    }

  }

  object instances extends Instances

}
