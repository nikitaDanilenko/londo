-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module LondoGQL.Object.IncompleteTaskStatistics exposing (..)

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


mean : SelectionSet LondoGQL.ScalarCodecs.BigDecimal LondoGQL.Object.IncompleteTaskStatistics
mean =
    Object.selectionForField "ScalarCodecs.BigDecimal" "mean" [] (LondoGQL.ScalarCodecs.codecs |> LondoGQL.Scalar.unwrapCodecs |> .codecBigDecimal |> .decoder)


total :
    SelectionSet decodesTo LondoGQL.Object.After
    -> SelectionSet decodesTo LondoGQL.Object.IncompleteTaskStatistics
total object____ =
    Object.selectionForCompositeField "total" [] object____ Basics.identity


counting :
    SelectionSet decodesTo LondoGQL.Object.After
    -> SelectionSet decodesTo LondoGQL.Object.IncompleteTaskStatistics
counting object____ =
    Object.selectionForCompositeField "counting" [] object____ Basics.identity
