module Pages.Registration.Confirm.Requests exposing (..)

import Configuration exposing (Configuration)
import Graphql.Http
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet(..))
import LondoGQL.InputObject exposing (CreationComplement)
import LondoGQL.Mutation as Mutation
import LondoGQL.Object.User
import LondoGQL.Object.UserId
import LondoGQL.Scalar exposing (Unit(..), Uuid(..))
import Pages.Registration.Confirm.Page as Page
import Types.Auxiliary exposing (JWT)


confirmRegistration : Configuration -> JWT -> CreationComplement -> Cmd Page.LogicMsg
confirmRegistration configuration jwt complement =
    Mutation.confirmRegistration
        { input =
            { creationToken = jwt
            , creationComplement = complement
            }
        }
        -- todo: Extract unit constant
        (SelectionSet.map (\_ -> Unit "()") (LondoGQL.Object.User.id LondoGQL.Object.UserId.uuid))
        |> Graphql.Http.mutationRequest configuration.graphQLEndpoint
        |> Graphql.Http.send Page.GotResponse
