package graphql.types

import errors.ServerError

trait FromInternal[+G, -Internal] {
  def from(internal: Internal): G
}

object FromInternal {
  def apply[G, Internal](implicit fromInternal: FromInternal[G, Internal]): FromInternal[G, Internal] = fromInternal

  implicit def serverErrorOrFromInternal[G, Internal](implicit
      fromInternal: FromInternal[G, Internal]
  ): FromInternal[ServerError.Or[G], ServerError.Or[Internal]] = error => error.map(fromInternal.from)

  implicit def serverErrorValidFromInternal[G, Internal](implicit
      fromInternal: FromInternal[G, Internal]
  ): FromInternal[ServerError.Valid[G], ServerError.Valid[Internal]] = error => error.map(fromInternal.from)

  object syntax {

    implicit class FromInternalConversion[Internal](val internal: Internal) extends AnyVal {
      def fromInternal[G](implicit fromInternal: FromInternal[G, Internal]): G = fromInternal.from(internal)
    }

    implicit class FromInternalServerErrorOr[Internal](val internal: ServerError.Or[Internal]) extends AnyVal {

      def fromInternal[G](implicit fromInternal: FromInternal[G, Internal]): ServerError.Or[G] =
        serverErrorOrFromInternal[G, Internal].from(internal)

    }

    implicit class FromInternalServerErrorValid[Internal](val internal: ServerError.Valid[Internal]) extends AnyVal {

      def fromInternal[G](implicit fromInternal: FromInternal[G, Internal]): ServerError.Valid[G] =
        serverErrorValidFromInternal[G, Internal].from(internal)

    }

  }

}
