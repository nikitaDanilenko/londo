package utils.jwt

import errors.{ ErrorContext, ServerError }
import graphql.types.FromInternal.syntax._
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
      .map(_ => ErrorContext.Authentication.Token.Decoding.asServerError)
      .flatMap { jwtClaim =>
        io.circe.parser
          .parse(jwtClaim.content)
          .flatMap(_.as[JwtContent])
          .left
          .map(_ => ErrorContext.Authentication.Token.Content.asServerError)
      }

  def createJwt(userId: UserId, privateKey: String, expiration: JwtExpiration): String =
    JwtCirce.encode(
      header = JwtHeader(algorithm = signatureAlgorithm),
      claim = JwtClaim(
        content = JwtContent(
          userId = userId.fromInternal
        ).asJson.noSpaces,
        expiration = expiration.expirationAt,
        notBefore = expiration.notBefore
      ),
      key = privateKey
    )

}
