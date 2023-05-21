module Pages.Util.Parent.Page exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Util.Editing as Editing exposing (Editing)
import Util.HttpUtil as HttpUtil


type alias Model parent update language =
    Tristate.Model (Main parent update language) (Initial parent language)


type alias Main parent update language =
    { jwt : JWT
    , parent : Editing parent update
    , language : language
    }


type alias Initial parent language =
    { jwt : JWT
    , parent : Maybe parent
    , language : language
    }


initialWith : JWT -> language -> Initial parent language
initialWith jwt language =
    { jwt = jwt
    , parent = Nothing
    , language = language
    }


initialToMain : Initial parent language -> Maybe (Main parent update language)
initialToMain i =
    i.parent
        |> Maybe.map
            (\parent ->
                { jwt = i.jwt
                , parent = parent |> Editing.asView
                , language = i.language
                }
            )


lenses :
    { initial :
        { parent : Lens (Initial parent language) (Maybe parent)
        }
    , main :
        { parent : Lens (Main parent update language) (Editing parent update)
        }
    }
lenses =
    { initial =
        { parent = Lens .parent (\b a -> { a | parent = b })
        }
    , main =
        { parent = Lens .parent (\b a -> { a | parent = b })
        }
    }


type LogicMsg parent update
    = GotFetchResponse (HttpUtil.GraphQLResult parent)
    | Edit update
    | SaveEdit
    | GotSaveEditResponse (HttpUtil.GraphQLResult parent)
    | EnterEdit
    | ExitEdit
    | RequestDelete
    | ConfirmDelete
    | CancelDelete
    | GotDeleteResponse (HttpUtil.GraphQLResult Bool)
    | ToggleControls
