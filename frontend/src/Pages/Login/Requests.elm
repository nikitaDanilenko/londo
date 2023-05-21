module Pages.Login.Requests exposing (login)

import Configuration exposing (Configuration)
import Graphql.Http
import LondoGQL.Mutation as Mutation
import Pages.Login.Page as Page
import Types.Credentials exposing (Credentials)


login : Configuration -> Credentials -> Cmd Page.LogicMsg
login configuration credentials =
    Mutation.login
        { input = credentials }
        |> Graphql.Http.mutationRequest configuration.graphQLEndpoint
        |> Graphql.Http.send Page.GotResponse
