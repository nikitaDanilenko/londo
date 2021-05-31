package utils.jwt

import errors.ServerError
import io.circe.syntax._
import pdi.jwt.algorithms.JwtAsymmetricAlgorithm
import pdi.jwt.{ JwtAlgorithm, JwtCirce, JwtClaim, JwtHeader }
import security.jwt.{ JwtContent, JwtExpiration }
import services.user.UserId

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
          userId = graphql.types.user.UserId.fromInternal(userId)
        ).asJson.noSpaces,
        expiration = expiration.expirationAt,
        notBefore = expiration.notBefore
      ),
      key = privateKey
    )

}
