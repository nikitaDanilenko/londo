-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module LondoGQL.Object.After exposing (..)

import Graphql.Internal.Builder.Argument as Argument exposing (Argument)
import Graphql.Internal.Builder.Object as Object
import Graphql.Internal.Encode as Encode exposing (Value)
import Graphql.Operation exposing (RootMutation, RootQuery, RootSubscription)
import Graphql.OptionalArgument exposing (OptionalArgument(..))
import Graphql.SelectionSet exposing (SelectionSet)
import Json.Decode as Decode
import LondoGQL.InputObject
import LondoGQL.Interface
import LondoGQL.Object
import LondoGQL.Scalar
import LondoGQL.ScalarCodecs
import LondoGQL.Union


one : SelectionSet LondoGQL.ScalarCodecs.BigDecimal LondoGQL.Object.After
one =
    Object.selectionForField "ScalarCodecs.BigDecimal" "one" [] (LondoGQL.ScalarCodecs.codecs |> LondoGQL.Scalar.unwrapCodecs |> .codecBigDecimal |> .decoder)


completion : SelectionSet LondoGQL.ScalarCodecs.BigDecimal LondoGQL.Object.After
completion =
    Object.selectionForField "ScalarCodecs.BigDecimal" "completion" [] (LondoGQL.ScalarCodecs.codecs |> LondoGQL.Scalar.unwrapCodecs |> .codecBigDecimal |> .decoder)


simulation : SelectionSet (Maybe LondoGQL.ScalarCodecs.BigDecimal) LondoGQL.Object.After
simulation =
    Object.selectionForField "(Maybe ScalarCodecs.BigDecimal)" "simulation" [] (LondoGQL.ScalarCodecs.codecs |> LondoGQL.Scalar.unwrapCodecs |> .codecBigDecimal |> .decoder |> Decode.nullable)
