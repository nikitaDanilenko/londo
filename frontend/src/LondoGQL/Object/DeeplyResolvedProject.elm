-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module LondoGQL.Object.DeeplyResolvedProject exposing (..)

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


project :
    SelectionSet decodesTo LondoGQL.Object.Project
    -> SelectionSet decodesTo LondoGQL.Object.DeeplyResolvedProject
project object____ =
    Object.selectionForCompositeField "project" [] object____ Basics.identity


tasks :
    SelectionSet decodesTo LondoGQL.Object.ResolvedTask
    -> SelectionSet (List decodesTo) LondoGQL.Object.DeeplyResolvedProject
tasks object____ =
    Object.selectionForCompositeField "tasks" [] object____ (Basics.identity >> Decode.list)
