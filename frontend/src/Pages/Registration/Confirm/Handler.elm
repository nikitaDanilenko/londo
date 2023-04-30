module Pages.Registration.Confirm.Handler exposing (init, update)

import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.Scalar exposing (Unit)
import Pages.Registration.Confirm.Page as Page
import Pages.Registration.Confirm.Requests as Requests
import Pages.Util.ComplementInput exposing (ComplementInput)
import Pages.View.Tristate as Tristate
import Result.Extra
import Util.HttpUtil as HttpUtil


init : Page.Flags -> ( Page.Model, Cmd Page.Msg )
init flags =
    ( Page.initial flags
    , Cmd.none
    )


update : Page.Msg -> Page.Model -> ( Page.Model, Cmd Page.Msg )
update =
    Tristate.updateWith updateLogic


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic msg model =
    case msg of
        Page.SetComplementInput complementInput ->
            setComplementInput model complementInput

        Page.Request ->
            request model

        Page.GotResponse result ->
            gotResponse model result


setComplementInput : Page.Model -> ComplementInput -> ( Page.Model, Cmd Page.LogicMsg )
setComplementInput model complementInput =
    ( model |> Tristate.mapMain (Page.lenses.main.complementInput.set complementInput)
    , Cmd.none
    )


request : Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
request model =
    ( model
    , model
        |> Tristate.foldMain Cmd.none
            (\main ->
                Requests.confirmRegistration model.configuration
                    main.registrationJWT
                    { password = main.complementInput.passwordInput.password1
                    , displayName = main.complementInput.displayName |> OptionalArgument.fromMaybe
                    }
            )
    )


gotResponse : Page.Model -> HttpUtil.GraphQLResult Unit -> ( Page.Model, Cmd Page.LogicMsg )
gotResponse model result =
    ( result
        |> Result.Extra.unpack
            (Tristate.toError model)
            (\_ -> model |> Tristate.mapMain (Page.lenses.main.mode.set Page.Confirmed))
    , Cmd.none
    )
