package security.jwt

sealed trait JwtExpiration {
  def notBefore: Option[Long]
  def expirationAt: Option[Long]
}

object JwtExpiration {

  case object Never extends JwtExpiration {
    override val notBefore: Option[Long] = None
    override val expirationAt: Option[Long] = None
  }

  case class Expiring(start: Long, duration: Long) extends JwtExpiration {
    override val notBefore: Option[Long] = Some(start)
    override val expirationAt: Option[Long] = Some(start + duration)
  }

}
