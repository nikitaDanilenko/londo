module Pages.Util.ParentEditor.Page exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.ParentEditor.Pagination as Pagination exposing (Pagination)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Util.DictList exposing (DictList)
import Util.Editing exposing (Editing)
import Util.HttpUtil as HttpUtil


type alias Model parentId parent creation update language =
    Tristate.Model (Main parentId parent creation update language) (Initial parentId parent update language)


type alias Main parentId parent creation update language =
    { jwt : JWT
    , parents : DictList parentId (Editing parent update)
    , parentCreation : Maybe creation
    , searchString : String
    , pagination : Pagination
    , language : language
    }


type alias Initial parentId parent update language =
    { jwt : JWT
    , parents : Maybe (DictList parentId (Editing parent update))
    , language : language
    }


initial : AuthorizedAccess -> language -> Model parentId parent creation update language
initial authorizedAccess language =
    { parents = Nothing
    , jwt = authorizedAccess.jwt
    , language = language
    }
        |> Tristate.createInitial authorizedAccess.configuration


initialToMain : Initial parentId parent update language -> Maybe (Main parentId parent creation update language)
initialToMain i =
    i.parents
        |> Maybe.map
            (\recipes ->
                { jwt = i.jwt
                , parents = recipes
                , parentCreation = Nothing
                , searchString = ""
                , pagination = Pagination.initial
                , language = i.language
                }
            )


lenses :
    { initial : { parents : Lens (Initial parentId parent update language) (Maybe (DictList parentId (Editing parent update))) }
    , main :
        { parents : Lens (Main parentId parent creation update language) (DictList parentId (Editing parent update))
        , parentCreation : Lens (Main parentId parent creation update language) (Maybe creation)
        , searchString : Lens (Main parentId parent creation update language) String
        , pagination : Lens (Main parentId parent creation update language) Pagination
        }
    }
lenses =
    { initial =
        { parents = Lens .parents (\b a -> { a | parents = b })
        }
    , main =
        { parents = Lens .parents (\b a -> { a | parents = b })
        , parentCreation = Lens .parentCreation (\b a -> { a | parentCreation = b })
        , searchString = Lens .searchString (\b a -> { a | searchString = b })
        , pagination = Lens .pagination (\b a -> { a | pagination = b })
        }
    }


type LogicMsg parentId parent creation update
    = UpdateCreation (Maybe creation)
    | Create
    | GotCreateResponse (HttpUtil.GraphQLResult parent)
    | Edit update
    | SaveEdit parentId
    | GotSaveEditResponse (HttpUtil.GraphQLResult parent)
    | ToggleControls parentId
    | EnterEdit parentId
    | ExitEdit parentId
    | RequestDelete parentId
    | ConfirmDelete parentId
    | CancelDelete parentId
    | GotDeleteResponse parentId (HttpUtil.GraphQLResult Bool)
    | GotFetchResponse (HttpUtil.GraphQLResult (List parent))
    | SetPagination Pagination
    | SetSearchString String
