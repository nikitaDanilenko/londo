package graphql.types

trait ToInternal[G, Internal] {
  def to(graphQL: G): Internal
}

object ToInternal {
  def apply[G, Internal](implicit toInternal: ToInternal[G, Internal]): ToInternal[G, Internal] = toInternal

  object syntax {

    implicit class ToInternalConversion[G](val g: G) extends AnyVal {
      def toInternal[Internal](implicit toInternal: ToInternal[G, Internal]): Internal = toInternal.to(g)
    }

  }

}
