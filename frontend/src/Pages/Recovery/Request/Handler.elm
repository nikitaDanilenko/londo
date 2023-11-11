module Pages.Recovery.Request.Handler exposing (init, update)

import Pages.Recovery.Request.Page as Page
import Pages.View.Tristate as Tristate
import Result.Extra
import Types.User.SearchResult
import Types.User.User


init : Page.Flags -> ( Page.Model, Cmd Page.Msg )
init flags =
    ( Page.initial flags.configuration
    , Cmd.none
    )


update : Page.Msg -> Page.Model -> ( Page.Model, Cmd Page.Msg )
update =
    Tristate.updateWith updateLogic


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic msg model =
    let
        find =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (.searchString >> Types.User.SearchResult.fetchWith Page.GotFindResponse model.configuration)
            )

        gotFindResponse result =
            result
                |> Result.Extra.unpack (\error -> ( Tristate.toError model error, Cmd.none ))
                    (\users ->
                        case users of
                            user :: [] ->
                                requestRecovery user.id

                            _ ->
                                ( model
                                    |> Tristate.mapMain
                                        (Page.lenses.main.users.set users
                                            >> Page.lenses.main.mode.set Page.Requesting
                                        )
                                , Cmd.none
                                )
                    )

        setSearchString string =
            ( model
                |> Tristate.mapMain
                    (Page.lenses.main.searchString.set string
                        >> Page.lenses.main.mode.set Page.Initial
                    )
            , Cmd.none
            )

        requestRecovery userId =
            ( model
            , Types.User.User.requestRecoveryWith
                Page.GotRequestRecoveryResponse
                model.configuration
                userId
            )

        gotRequestRecoveryResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\_ -> model |> Tristate.mapMain (Page.lenses.main.mode.set Page.Requested))
            , Cmd.none
            )
    in
    case msg of
        Page.Find ->
            find

        Page.GotFindResponse result ->
            gotFindResponse result

        Page.SetSearchString string ->
            setSearchString string

        Page.RequestRecovery userId ->
            requestRecovery userId

        Page.GotRequestRecoveryResponse result ->
            gotRequestRecoveryResponse result
