package graphql.types

trait FromInternal[+G, -Internal] {
  def from(internal: Internal): G
}

object FromInternal {
  def apply[G, Internal](implicit fromInternal: FromInternal[G, Internal]): FromInternal[G, Internal] = fromInternal

  object syntax {

    implicit class FromInternalConversion[Internal](val internal: Internal) extends AnyVal {
      def fromInternal[G](implicit fromInternal: FromInternal[G, Internal]): G = fromInternal.from(internal)
    }

  }

}
