module Pages.Register.CreateNewUser exposing (Flags, Model, Msg, init, update, view)

import Configuration exposing (Configuration)
import Graphql.Http
import Html exposing (Html, button, div, input, label, text)
import Html.Attributes exposing (class, disabled, for, id, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Language.Language exposing (Language)
import LondoGQL.InputObject
import LondoGQL.Mutation as Mutation
import LondoGQL.Object.User
import Pages.Register.NewUser as NewUser exposing (NewUser, NewUserField(..))
import Pages.Util.Links exposing (linkButton)
import Pages.Util.TriState as TriState exposing (TriState(..))
import RemoteData exposing (RemoteData)
import Url.Builder as UrlBuilder


type alias Model =
    { email : String
    , token : String
    , language : Language
    , newUser : NewUser
    , state : TriState
    , configuration : Configuration
    }


updateNewUser : Model -> NewUser -> Model
updateNewUser model newUser =
    { model | newUser = newUser }


updateState : Model -> TriState -> Model
updateState model state =
    { model | state = state }


type alias Flags =
    { email : String, token : String, language : Language, configuration : Configuration }


type Msg
    = SetNewUserField NewUserField String
    | CreateUser
    | GotResponse (RemoteData (Graphql.Http.Error String) String)


init : Flags -> ( Model, Cmd Msg )
init flags =
    let
        model =
            { email = flags.email
            , token = flags.token
            , language = flags.language
            , newUser = NewUser.empty
            , state = TriState.Initial
            , configuration = flags.configuration
            }
    in
    ( model, Cmd.none )


view : Model -> Html Msg
view md =
    case md.state of
        Initial ->
            let
                createOnEnter =
                    onEnter CreateUser
            in
            div [ id "creatingUserView" ]
                [ div [ id "creatingUser" ]
                    [ label [ for "nickname" ] [ text md.language.userCreation.nickname ]
                    , input
                        [ onInput (SetNewUserField UserField)
                        , value md.newUser.nickname
                        , createOnEnter
                        ]
                        []
                    ]
                , div [ id "creatingPassword1" ]
                    [ label [ for "password1" ] [ text md.language.userCreation.password1 ]
                    , input
                        [ onInput (SetNewUserField PasswordField1)
                        , value md.newUser.password1
                        , type_ "password"
                        , createOnEnter
                        ]
                        []
                    ]
                , div [ id "creatingPassword2" ]
                    [ label [ for "password2" ] [ text md.language.userCreation.password2 ]
                    , input
                        [ onInput (SetNewUserField PasswordField2)
                        , value md.newUser.password2
                        , type_ "password"
                        , createOnEnter
                        ]
                        []
                    ]
                , button
                    [ class "button"
                    , onClick CreateUser
                    , disabled (not (NewUser.isValid md.newUser))
                    ]
                    [ text "Create" ]
                ]

        Success ->
            div [ id "createdUser" ]
                [ text md.language.userCreation.success
                , linkButton
                    { url = UrlBuilder.relative [ md.configuration.mainPageURL, md.configuration.subFolders.login ] []
                    , attributes = [ class "navigationButton" ]
                    , children = [ text md.language.userCreation.loginPageLinkText ]
                    , isDisabled = False
                    }
                ]

        Failure ->
            --todo: Add error handling
            div [] []


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        SetNewUserField newUserField string ->
            let
                newModel =
                    NewUser.update model.newUser newUserField string
                        |> updateNewUser model
            in
            ( newModel, Cmd.none )

        CreateUser ->
            ( model
            , model |> createNewUserCreationArguments |> createNewUser model.configuration.graphQLEndpoint
            )

        GotResponse remoteData ->
            ( updateState model (TriState.fromRemoteData remoteData), Cmd.none )


createNewUserCreationArguments : Model -> Mutation.CreateUserRequiredArguments
createNewUserCreationArguments model =
    { userCreation =
        { nickname = model.newUser.nickname
        , email = model.email
        , password = model.newUser.password1
        , token = model.token
        }
    }


createNewUser : String -> Mutation.CreateUserRequiredArguments -> Cmd Msg
createNewUser endpoint userCreation =
    Mutation.createUser userCreation LondoGQL.Object.User.nickname
        |> Graphql.Http.mutationRequest endpoint
        |> Graphql.Http.send (RemoteData.fromResult >> GotResponse)
