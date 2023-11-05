-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module LondoGQL.Object.TaskAnalysis exposing (..)

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


task :
    SelectionSet decodesTo LondoGQL.Object.Task
    -> SelectionSet decodesTo LondoGQL.Object.TaskAnalysis
task object____ =
    Object.selectionForCompositeField "task" [] object____ Basics.identity


simulation :
    SelectionSet decodesTo LondoGQL.Object.Simulation
    -> SelectionSet (Maybe decodesTo) LondoGQL.Object.TaskAnalysis
simulation object____ =
    Object.selectionForCompositeField "simulation" [] object____ (Basics.identity >> Decode.nullable)


incompleteStatistics :
    SelectionSet decodesTo LondoGQL.Object.IncompleteTaskStatistics
    -> SelectionSet (Maybe decodesTo) LondoGQL.Object.TaskAnalysis
incompleteStatistics object____ =
    Object.selectionForCompositeField "incompleteStatistics" [] object____ (Basics.identity >> Decode.nullable)
