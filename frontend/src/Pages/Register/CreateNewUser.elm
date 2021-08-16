module Pages.Register.CreateNewUser exposing (..)

import Configuration
import Graphql.Http
import Graphql.Operation exposing (RootMutation)
import Graphql.SelectionSet exposing (SelectionSet)
import Html exposing (Html, button, div, input, label, text)
import Html.Attributes exposing (class, disabled, for, id, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Language.Language exposing (Language)
import LondoGQL.InputObject as InputObject
import LondoGQL.Mutation as Mutation
import LondoGQL.Object exposing (User)
import Pages.Register.NewUser as NewUser exposing (NewUser, NewUserField(..))
import RemoteData


type alias Model =
    { email : String
    , token : String
    , language : Language
    , newUser : NewUser
    , state : State
    }


type State
    = Initial
    | Success
    | Failure


updateNewUser : Model -> NewUser -> Model
updateNewUser model newUser =
    { model | newUser = newUser }


updateState : Model -> State -> Model
updateState model state =
    { model | state = state }


type alias Flags =
    { email : String, token : String, language : Language }


type Msg
    = SetNewUserField NewUserField String
    | CreateUser
    | GotResponse User


init : Flags -> ( Model, Cmd Msg )
init flags =
    let
        model =
            { email = flags.email, token = flags.token, language = flags.language, newUser = NewUser.empty, state = Initial }
    in
    ( model, Cmd.none )


view : Model -> Html Msg
view md =
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
            , createNewUser
                { userCreation =
                    { nickname = model.newUser.nickname
                    , email = model.email
                    , password = model.newUser.password1
                    , token = model.token
                    }
                }
            )


--createNewUserQuery : InputObject.UserCreation -> SelectionSet User RootMutation
createNewUserQuery : Mutation.CreateUserRequiredArguments -> SelectionSet decodesTo User -> SelectionSet decodesTo RootMutation
createNewUserQuery userCreation =
    Mutation.createUser userCreation


createNewUser : Mutation.CreateUserRequiredArguments -> Cmd Msg
createNewUser model =
    createNewUserQuery model
        |> Graphql.Http.mutationRequest Configuration.graphQLEndpoint
        |> Graphql.Http.send (RemoteData.fromResult >> GotResponse)
