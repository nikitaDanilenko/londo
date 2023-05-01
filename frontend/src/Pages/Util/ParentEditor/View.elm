module Pages.Util.ParentEditor.View exposing (..)

import Configuration exposing (Configuration)
import Html exposing (Attribute, Html, button, div, table, tbody, td, text, th, thead, tr)
import Html.Attributes exposing (colspan, disabled)
import Html.Events exposing (onClick)
import Maybe.Extra
import Monocle.Compose as Compose
import Pages.Util.HtmlUtil as HtmlUtil
import Pages.Util.PaginationSettings as PaginationSettings
import Pages.Util.ParentEditor.Page as Page
import Pages.Util.ParentEditor.Pagination as Pagination exposing (Pagination)
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Paginate
import Util.DictList as DictList
import Util.Editing as Editing exposing (Editing)
import Util.MaybeUtil as MaybeUtil


viewParentsWith :
    { currentPage : ViewUtil.Page
    , matchesSearchText : String -> parent -> Bool
    , sort : List (Editing parent update) -> List (Editing parent update)
    , tableHeader : Html msg
    , viewLine : Configuration -> parent -> Bool -> List (Html msg)
    , updateLine : parent -> update -> List (Html msg)
    , deleteLine : parent -> List (Html msg)
    , create :
        { ifCreating : creation -> List (Html msg)
        , default : creation
        , label : String
        , update : Maybe creation -> msg
        }
    , setSearchString : String -> msg
    , setPagination : Pagination -> msg
    , styling : Attribute msg
    }
    -> Configuration
    -> Page.Main parentId parent creation update
    -> Html msg
viewParentsWith ps configuration main =
    ViewUtil.viewMainWith
        { configuration = configuration
        , jwt = .jwt >> Just
        , currentPage = Just ps.currentPage
        , showNavigation = True
        }
        main
    <|
        let
            viewParent =
                Editing.unpack
                    { onView = ps.viewLine configuration
                    , onUpdate = ps.updateLine
                    , onDelete = ps.deleteLine
                    }

            viewParents =
                main.parents
                    |> DictList.filter
                        (\_ v ->
                            ps.matchesSearchText main.searchString v.original
                        )
                    |> DictList.values
                    |> ps.sort
                    |> ViewUtil.paginate
                        { pagination = Page.lenses.main.pagination |> Compose.lensWithLens Pagination.lenses.parents
                        }
                        main

            ( button, creationLine ) =
                main.parentCreation
                    |> Maybe.Extra.unwrap
                        ( [ div [ Style.ids.add ]
                                [ creationButton
                                    { defaultCreation = ps.create.default
                                    , label = ps.create.label
                                    , updateCreationMsg = ps.create.update
                                    }
                                ]
                          ]
                        , []
                        )
                        (ps.create.ifCreating >> Tuple.pair [])
        in
        div [ ps.styling ]
            (button
                ++ [ HtmlUtil.searchAreaWith
                        { msg = ps.setSearchString
                        , searchString = main.searchString
                        }
                   , table [ Style.classes.elementsWithControlsTable ]
                        (ps.tableHeader
                            :: [ tbody []
                                    (creationLine
                                        ++ (viewParents |> Paginate.page |> List.concatMap viewParent)
                                    )
                               ]
                        )
                   , div [ Style.classes.pagination ]
                        [ ViewUtil.pagerButtons
                            { msg =
                                PaginationSettings.updateCurrentPage
                                    { pagination = Page.lenses.main.pagination
                                    , items = Pagination.lenses.parents
                                    }
                                    main
                                    >> ps.setPagination
                            , elements = viewParents
                            }
                        ]
                   ]
            )


tableHeaderWith :
    { columns : List (Html msg)
    , style : Attribute msg
    }
    -> Html msg
tableHeaderWith ps =
    thead []
        [ tr [ Style.classes.tableHeader, ps.style ]
            (ps.columns
                ++ [ th [ Style.classes.toggle ] []
                   ]
            )
        ]


lineWith :
    { rowWithControls : parent -> HtmlUtil.RowWithControls msg
    , toggleMsg : msg
    , showControls : Bool
    }
    -> parent
    -> List (Html msg)
lineWith ps parent =
    let
        row =
            parent |> ps.rowWithControls

        displayColumns =
            row
                |> .display
                |> List.map (HtmlUtil.withExtraAttributes [ ps.toggleMsg |> onClick ])

        infoRow =
            tr [ Style.classes.editing ]
                (displayColumns
                    ++ [ HtmlUtil.toggleControlsCell ps.toggleMsg ]
                )

        controlsRow =
            tr []
                [ td [ colspan <| List.length <| displayColumns ]
                    [ table [ Style.classes.elementsWithControlsTable ]
                        [ tr [] row.controls ]
                    ]
                ]
    in
    infoRow
        :: (if ps.showControls then
                [ controlsRow ]

            else
                []
           )


creationButton :
    { defaultCreation : creation
    , label : String
    , updateCreationMsg : Maybe creation -> msg
    }
    -> Html msg
creationButton ps =
    button
        [ Style.classes.button.add
        , onClick <| ps.updateCreationMsg <| Just <| ps.defaultCreation
        ]
        [ text <| ps.label ]


type alias LabelledButton msg =
    { msg : msg
    , name : String
    }


controlsRowWith :
    { colspan : Int
    , validInput : Bool
    , confirm : LabelledButton msg
    , cancel : LabelledButton msg
    }
    -> Html msg
controlsRowWith ps =
    tr []
        [ td [ colspan <| ps.colspan ]
            [ table [ Style.classes.elementsWithControlsTable ]
                [ tr []
                    [ td [ Style.classes.controls ]
                        [ button
                            ([ MaybeUtil.defined <| Style.classes.button.confirm
                             , MaybeUtil.defined <| disabled <| not <| ps.validInput
                             , MaybeUtil.optional ps.validInput <| onClick ps.confirm.msg
                             ]
                                |> Maybe.Extra.values
                            )
                            [ text <| ps.confirm.name ]
                        ]
                    , td [ Style.classes.controls ]
                        [ button [ Style.classes.button.cancel, onClick <| ps.cancel.msg ]
                            [ text <| ps.cancel.name ]
                        ]
                    ]
                ]
            ]
        ]
