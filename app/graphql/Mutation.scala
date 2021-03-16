package graphql

trait Mutation {}

object Mutation {
  def apply(graphQLServices: GraphQLServices): Mutation = new Mutation {}
}
