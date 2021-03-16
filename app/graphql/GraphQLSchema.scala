package graphql

import sangria.macros.derive.deriveContextObjectType
import sangria.schema.{ ObjectType, Schema }

import javax.inject.Singleton

@Singleton
class GraphQLSchema {

  val QueryType: ObjectType[GraphQLContext, Unit] =
    deriveContextObjectType[GraphQLContext, Query, Unit](_.query)

//  val MutationType: ObjectType[GraphQLContext, Unit] =
//    deriveContextObjectType[GraphQLContext, Mutation, Unit](_.mutation)

  val schema: Schema[GraphQLContext, Unit] = Schema(
    query = QueryType /*,
    mutation = Some(MutationType)*/
    /*,
    additionalTypes = ???*/
  )

}
