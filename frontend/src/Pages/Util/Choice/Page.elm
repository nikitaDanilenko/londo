module Pages.Util.Choice.Page exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.Util.Choice.Pagination as Pagination exposing (Pagination)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Util.DictList as DictList exposing (DictList)
import Util.Editing as Editing exposing (Editing)
import Util.HttpUtil as HttpUtil


{-|

  - elements are the values
  - elements are chosen from a given list of choices
  - a choice is made via a creation, i.e. 'creation' turns a choice into an element
  - an element can be updated with an 'update'
  - elements (may) belong to a parent

-}
type alias Model parentId elementId element update choiceId choice creation language =
    Tristate.Model (Main parentId elementId element update choiceId choice creation language) (Initial parentId elementId element choiceId choice language)


type alias Main parentId elementId element update choiceId choice creation language =
    { jwt : JWT
    , parentId : parentId
    , elements : DictList elementId (Editing element update)
    , choices : DictList choiceId (Editing choice creation)
    , pagination : Pagination
    , choicesSearchString : String
    , elementsSearchString : String
    , language : language
    }


type alias Initial parentId elementId element choiceId choice language =
    { jwt : JWT
    , parentId : parentId
    , elements : Maybe (DictList elementId element)
    , choices : Maybe (DictList choiceId choice)
    , language : language
    }


initialWith : JWT -> parentId -> language -> Initial parentId elementId element choiceId choice language
initialWith jwt parentId language =
    { jwt = jwt
    , parentId = parentId
    , elements = Nothing
    , choices = Nothing
    , language = language
    }


initialToMain : Initial parentId elementId element choiceId choice language -> Maybe (Main parentId elementId element update choiceId choice creation language)
initialToMain i =
    Maybe.map2
        (\elements choices ->
            { jwt = i.jwt
            , parentId = i.parentId
            , elements = elements |> DictList.map Editing.asView
            , choices = choices |> DictList.map Editing.asView
            , pagination = Pagination.initial
            , choicesSearchString = ""
            , elementsSearchString = ""
            , language = i.language
            }
        )
        i.elements
        i.choices


lenses :
    { initial :
        { elements : Lens (Initial parentId elementId element choiceId choice language) (Maybe (DictList elementId element))
        , choices : Lens (Initial parentId elementId element choiceId choice language) (Maybe (DictList choiceId choice))
        }
    , main :
        { elements : Lens (Main parentId elementId element update choiceId choice creation language) (DictList elementId (Editing element update))
        , choices : Lens (Main parentId elementId element update choiceId choice creation language) (DictList choiceId (Editing choice creation))
        , pagination : Lens (Main parentId elementId element update choiceId choice creation language) Pagination
        , choicesSearchString : Lens (Main parentId elementId element update choiceId choice creation language) String
        , elementsSearchString : Lens (Main parentId elementId element update choiceId choice creation language) String
        }
    }
lenses =
    { initial =
        { elements = Lens .elements (\b a -> { a | elements = b })
        , choices = Lens .choices (\b a -> { a | choices = b })
        }
    , main =
        { elements = Lens .elements (\b a -> { a | elements = b })
        , choices = Lens .choices (\b a -> { a | choices = b })
        , pagination = Lens .pagination (\b a -> { a | pagination = b })
        , choicesSearchString = Lens .choicesSearchString (\b a -> { a | choicesSearchString = b })
        , elementsSearchString = Lens .elementsSearchString (\b a -> { a | elementsSearchString = b })
        }
    }


type LogicMsg elementId element update choiceId choice creation
    = Edit update
    | SaveEdit update
    | GotSaveEditResponse (HttpUtil.GraphQLResult element)
    | ToggleControls elementId
    | EnterEdit elementId
    | ExitEdit elementId
    | RequestDelete elementId
    | ConfirmDelete elementId
    | CancelDelete elementId
    | GotDeleteResponse elementId (HttpUtil.GraphQLResult Bool)
    | GotFetchElementsResponse (HttpUtil.GraphQLResult (List element))
    | GotFetchChoicesResponse (HttpUtil.GraphQLResult (List choice))
    | ToggleChoiceControls choiceId
    | SelectChoice choice
    | DeselectChoice choiceId
    | Create choiceId
    | GotCreateResponse (HttpUtil.GraphQLResult element)
    | UpdateCreation creation
    | SetPagination Pagination
    | SetElementsSearchString String
    | SetChoicesSearchString String
