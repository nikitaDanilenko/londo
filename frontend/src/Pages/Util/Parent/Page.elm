module Pages.Util.Parent.Page exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Util.Editing as Editing exposing (Editing)
import Util.HttpUtil as HttpUtil


type alias Model parent update =
    Tristate.Model (Main parent update) (Initial parent)


type alias Main parent update =
    { jwt : JWT
    , parent : Editing parent update
    }


type alias Initial parent =
    { jwt : JWT
    , parent : Maybe parent
    }


initialWith : JWT -> Initial parent
initialWith jwt =
    { jwt = jwt
    , parent = Nothing
    }


initialToMain : Initial parent -> Maybe (Main parent update)
initialToMain i =
    i.parent
        |> Maybe.map
            (\parent ->
                { jwt = i.jwt
                , parent = parent |> Editing.asView
                }
            )


lenses :
    { initial :
        { parent : Lens (Initial parent) (Maybe parent)
        }
    , main :
        { parent : Lens (Main parent update) (Editing parent update)
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
    | GotDeleteResponse (HttpUtil.GraphQLResult ())
    | ToggleControls
