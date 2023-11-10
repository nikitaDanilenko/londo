module Pages.Settings.Page exposing (..)

import LondoGQL.Enum.LogoutMode
import LondoGQL.Scalar
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.ComplementInput as ComplementInput exposing (ComplementInput)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Types.User.User
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main Initial


type alias Main =
    { jwt : JWT
    , user : Types.User.User.User
    , complementInput : ComplementInput
    , mode : Mode
    }


type alias Initial =
    { jwt : JWT
    , user : Maybe Types.User.User.User
    }


initial : AuthorizedAccess -> Model
initial authorizedAccess =
    { jwt = authorizedAccess.jwt
    , user = Nothing
    }
        |> Tristate.createInitial authorizedAccess.configuration


initialToMain : Initial -> Maybe Main
initialToMain i =
    Maybe.map
        (\user ->
            { jwt = i.jwt
            , user = user
            , complementInput = ComplementInput.initial
            , mode = Regular
            }
        )
        i.user


lenses :
    { initial :
        { user : Lens Initial (Maybe Types.User.User.User)
        }
    , main :
        { user : Lens Main Types.User.User.User
        , complementInput : Lens Main ComplementInput
        , mode : Lens Main Mode
        }
    }
lenses =
    { initial = { user = Lens .user (\b a -> { a | user = b }) }
    , main =
        { user = Lens .user (\b a -> { a | user = b })
        , complementInput = Lens .complementInput (\b a -> { a | complementInput = b })
        , mode = Lens .mode (\b a -> { a | mode = b })
        }
    }


type Mode
    = Regular
    | RequestedDeletion


type alias Flags =
    { authorizedAccess : AuthorizedAccess
    }


type alias Msg =
    Tristate.Msg LogicMsg


type LogicMsg
    = GotFetchUserResponse (HttpUtil.GraphQLResult Types.User.User.User)
    | UpdatePassword
    | GotUpdatePasswordResponse (HttpUtil.GraphQLResult Bool)
    | UpdateSettings
    | GotUpdateSettingsResponse (HttpUtil.GraphQLResult Types.User.User.User)
    | RequestDeletion
    | GotRequestDeletionResponse (HttpUtil.GraphQLResult LondoGQL.Scalar.Unit)
    | SetComplementInput ComplementInput
    | Logout LondoGQL.Enum.LogoutMode.LogoutMode
    | GotLogoutResponse (HttpUtil.GraphQLResult Bool)
