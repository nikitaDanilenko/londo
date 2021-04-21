package utils.jwt

import db.keys.UserId
import errors.ServerError
import io.circe.syntax._
import pdi.jwt.algorithms.JwtAsymmetricAlgorithm
import pdi.jwt.{ JwtAlgorithm, JwtCirce, JwtClaim, JwtHeader }
import security.jwt.{ JwtContent, JwtExpiration }

object JwtUtil {

  val signatureAlgorithm: JwtAsymmetricAlgorithm = JwtAlgorithm.RS256

  def validateJwt(token: String, publicKey: String): ServerError.Or[JwtContent] =
    JwtCirce
      .decode(token, publicKey, Seq(signatureAlgorithm))
      .toEither
      .left
      .map(_ => ServerError.Authentication.Token.Decoding)
      .flatMap { jwtClaim =>
        io.circe.parser
          .parse(jwtClaim.content)
          .flatMap(_.as[JwtContent])
          .left
          .map(_ => ServerError.Authentication.Token.Content)
      }

  def createJwt(userId: UserId, privateKey: String, expiration: JwtExpiration): String =
    JwtCirce.encode(
      header = JwtHeader.apply(signatureAlgorithm),
      claim = JwtClaim(
        content = JwtContent(
          userId = userId
        ).asJson.noSpaces,
        expiration = expiration.expirationAt,
        notBefore = expiration.notBefore
      ),
      key = privateKey
    )

}
