module Pages.Registration.Request.Requests exposing (..)

import Configuration exposing (Configuration)
import Graphql.Http
import LondoGQL.Mutation as Mutation
import Pages.Registration.Request.Page as Page
import Types.Auxiliary exposing (UserIdentifier)


requestRegistration : Configuration -> UserIdentifier -> Cmd Page.LogicMsg
requestRegistration configuration userIdentifier =
    Mutation.requestRegistration
        { input =
            { userIdentifier = userIdentifier
            }
        }
        |> Graphql.Http.mutationRequest configuration.graphQLEndpoint
        |> Graphql.Http.send Page.GotRequestResponse
