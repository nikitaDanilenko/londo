module GraphQLFunctions.Lens.PlainCreation exposing (..)

import GraphQLFunctions.OptionalArgumentUtil as OptionalArgument
import Graphql.OptionalArgument as OptionalArgumentUtil
import LondoGQL.Enum.TaskKind exposing (TaskKind)
import LondoGQL.InputObject exposing (PlainCreation, ProgressInput)
import LondoGQL.Scalar exposing (Positive)
import Monocle.Lens exposing (Lens)


name : Lens PlainCreation String
name =
    Lens .name (\b a -> { a | name = b })


taskKind : Lens PlainCreation TaskKind
taskKind =
    Lens .taskKind (\b a -> { a | taskKind = b })


unit : Lens PlainCreation (Maybe String)
unit =
    Lens (.unit >> OptionalArgument.toMaybe) (\b a -> { a | unit = OptionalArgumentUtil.fromMaybe b })


progress : Lens PlainCreation ProgressInput
progress =
    Lens .progress (\b a -> { a | progress = b })


weight : Lens PlainCreation Positive
weight =
    Lens .weight (\b a -> { a | weight = b })
