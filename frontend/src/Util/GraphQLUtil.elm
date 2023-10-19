module Util.GraphQLUtil exposing (..)

import BigInt exposing (BigInt)
import LondoGQL.Scalar


bigIntFromGraphQL : LondoGQL.Scalar.BigInt -> Maybe BigInt
bigIntFromGraphQL (LondoGQL.Scalar.BigInt x) =
    x |> BigInt.fromIntString


bigIntToGraphQL : BigInt -> LondoGQL.Scalar.BigInt
bigIntToGraphQL =
    BigInt.toString >> LondoGQL.Scalar.BigInt
