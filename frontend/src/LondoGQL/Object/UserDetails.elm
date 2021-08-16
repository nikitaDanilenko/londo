-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module LondoGQL.Object.UserDetails exposing (..)

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


firstName : SelectionSet (Maybe String) LondoGQL.Object.UserDetails
firstName =
    Object.selectionForField "(Maybe String)" "firstName" [] (Decode.string |> Decode.nullable)


lastName : SelectionSet (Maybe String) LondoGQL.Object.UserDetails
lastName =
    Object.selectionForField "(Maybe String)" "lastName" [] (Decode.string |> Decode.nullable)


description : SelectionSet (Maybe String) LondoGQL.Object.UserDetails
description =
    Object.selectionForField "(Maybe String)" "description" [] (Decode.string |> Decode.nullable)