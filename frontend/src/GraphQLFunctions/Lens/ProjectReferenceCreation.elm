module GraphQLFunctions.Lens.ProjectReferenceCreation exposing (..)

import LondoGQL.InputObject exposing (ProjectReferenceCreation)
import LondoGQL.Scalar exposing (Positive, Uuid)
import Monocle.Lens exposing (Lens)


weight : Lens ProjectReferenceCreation Positive
weight =
    Lens .weight (\b a -> { a | weight = b })


projectReferenceId : Lens ProjectReferenceCreation Uuid
projectReferenceId =
    Lens (.projectReferenceId >> .uuid) (\b a -> { a | projectReferenceId = { uuid = b } })
