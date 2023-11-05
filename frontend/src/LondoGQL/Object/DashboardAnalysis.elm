-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module LondoGQL.Object.DashboardAnalysis exposing (..)

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


dashboard :
    SelectionSet decodesTo LondoGQL.Object.Dashboard
    -> SelectionSet decodesTo LondoGQL.Object.DashboardAnalysis
dashboard object____ =
    Object.selectionForCompositeField "dashboard" [] object____ Basics.identity


projectAnalyses :
    SelectionSet decodesTo LondoGQL.Object.ProjectAnalysis
    -> SelectionSet (List decodesTo) LondoGQL.Object.DashboardAnalysis
projectAnalyses object____ =
    Object.selectionForCompositeField "projectAnalyses" [] object____ (Basics.identity >> Decode.list)


dashboardStatistics :
    SelectionSet decodesTo LondoGQL.Object.DashboardStatistics
    -> SelectionSet decodesTo LondoGQL.Object.DashboardAnalysis
dashboardStatistics object____ =
    Object.selectionForCompositeField "dashboardStatistics" [] object____ Basics.identity