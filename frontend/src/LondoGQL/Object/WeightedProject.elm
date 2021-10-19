-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module LondoGQL.Object.WeightedProject exposing (..)

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


resolvedProject :
    SelectionSet decodesTo LondoGQL.Object.ResolvedProject
    -> SelectionSet decodesTo LondoGQL.Object.WeightedProject
resolvedProject object____ =
    Object.selectionForCompositeField "resolvedProject" [] object____ Basics.identity


weight : SelectionSet LondoGQL.ScalarCodecs.Positive LondoGQL.Object.WeightedProject
weight =
    Object.selectionForField "ScalarCodecs.Positive" "weight" [] (LondoGQL.ScalarCodecs.codecs |> LondoGQL.Scalar.unwrapCodecs |> .codecPositive |> .decoder)
