package graphql.types

trait FromAndToInternal[G, Internal] extends FromInternal[G, Internal] with ToInternal[G, Internal]

object FromAndToInternal {

  def create[G, Internal](fromInternal: Internal => G, toInternal: G => Internal): FromAndToInternal[G, Internal] =
    new FromAndToInternal[G, Internal] {
      override def from(internal: Internal): G = fromInternal(internal)
      override def to(graphQL: G): Internal = toInternal(graphQL)
    }

}
