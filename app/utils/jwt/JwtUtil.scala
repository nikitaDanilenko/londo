package utils.jwt

import errors.{ ErrorContext, ServerError }
import io.circe.Encoder
import io.circe.syntax._
import pdi.jwt.algorithms.JwtAsymmetricAlgorithm
import pdi.jwt.{ JwtAlgorithm, JwtCirce, JwtClaim, JwtHeader }
import security.jwt.{ LoggedIn, JwtExpiration }

object JwtUtil {

  private val signatureAlgorithm: JwtAsymmetricAlgorithm = JwtAlgorithm.RS256

  def validateJwt(token: String, publicKey: String): ServerError.Or[LoggedIn] =
    JwtCirce
      .decode(token, publicKey, Seq(signatureAlgorithm))
      .toEither
      .left
      .map(_ => ErrorContext.Authentication.Token.Decoding.asServerError)
      .flatMap { jwtClaim =>
        io.circe.parser
          .decode[LoggedIn](jwtClaim.content)
          .left
          .map(_ => ErrorContext.Authentication.Token.Content.asServerError)
      }

  def createJwt[A: Encoder](content: A, privateKey: String, expiration: JwtExpiration): String =
    JwtCirce.encode(
      header = JwtHeader(algorithm = signatureAlgorithm),
      claim = JwtClaim(
        content = content.asJson.noSpaces,
        expiration = expiration.expirationAt,
        notBefore = expiration.notBefore
      ),
      key = privateKey
    )

}
