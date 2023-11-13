module Pages.Util.ViewUtil exposing (Page(..), navigationBarWith, navigationToPageButton, navigationToPageButtonWith, pagerButtons, paginate, viewMainWith)

import Addresses.Frontend
import Configuration exposing (Configuration)
import Html exposing (Attribute, Html, button, header, main_, nav, p, table, tbody, td, text, tr)
import Html.Attributes exposing (disabled)
import Html.Events exposing (onClick)
import Maybe.Extra
import Monocle.Lens exposing (Lens)
import Pages.Util.Links as Links
import Pages.Util.PaginationSettings as PaginationSettings exposing (PaginationSettings)
import Pages.Util.Style as Style
import Paginate exposing (PaginatedList)
import Url.Builder
import Util.MaybeUtil as MaybeUtil


viewMainWith :
    { configuration : Configuration
    , currentPage : Maybe Page
    , showNavigation : Bool
    , id : Attribute msg
    }
    -> List (Html msg)
    -> List (Html msg)
viewMainWith ps html =
    let
        navigation =
            [ navigationBar
                { mainPageURL = ps.configuration |> .mainPageURL
                , currentPage = ps.currentPage
                }
            ]
                |> List.filter (always ps.showNavigation)
    in
    [ header [] navigation
    , main_ [ ps.id ] html
    ]


type Page
    = Projects
    | Dashboards
    | UserSettings
    | Login
    | Overview


navigationPages : List Page
navigationPages =
    [ Projects, Dashboards, UserSettings ]


addressSuffix : Page -> String
addressSuffix page =
    let
        parts =
            case page of
                Projects ->
                    Addresses.Frontend.projects.address ()

                Dashboards ->
                    Addresses.Frontend.dashboards.address ()

                UserSettings ->
                    Addresses.Frontend.settings.address ()

                Login ->
                    Addresses.Frontend.login.address ()

                Overview ->
                    Addresses.Frontend.overview.address ()
    in
    parts |> String.concat


nameOf : Page -> String
nameOf page =
    case page of
        Projects ->
            "Projects"

        Dashboards ->
            "Dashboards"

        UserSettings ->
            "Settings"

        Login ->
            "Login"

        Overview ->
            "Overview"


navigationLink : { mainPageURL : String, page : String } -> String
navigationLink ps =
    Url.Builder.relative
        [ ps.mainPageURL
        , "#"
        , ps.page
        ]
        []


navigationToPageButton :
    { page : Page
    , mainPageURL : String
    , currentPage : Maybe Page
    }
    -> Html msg
navigationToPageButton ps =
    navigationToPageButtonWith
        { page = ps.page
        , nameOf = nameOf
        , addressSuffix = addressSuffix
        , mainPageURL = ps.mainPageURL
        , currentPage = ps.currentPage
        }


navigationToPageButtonWith :
    { page : page
    , nameOf : page -> String
    , addressSuffix : page -> String
    , mainPageURL : String
    , currentPage : Maybe page
    }
    -> Html msg
navigationToPageButtonWith ps =
    let
        isDisabled =
            Maybe.Extra.unwrap False (\current -> current == ps.page) ps.currentPage

        navigationAttributes =
            [ Style.classes.button.navigation
            ]
    in
    if isDisabled then
        p (Style.classes.disabled :: navigationAttributes) [ text <| ps.nameOf <| ps.page ]

    else
        Links.linkButton
            { url =
                navigationLink
                    { mainPageURL = ps.mainPageURL
                    , page = ps.addressSuffix ps.page
                    }
            , attributes = navigationAttributes
            , linkText = ps.nameOf <| ps.page
            }


navigationBar :
    { mainPageURL : String
    , currentPage : Maybe Page
    }
    -> Html msg
navigationBar ps =
    navigationBarWith
        { navigationPages = navigationPages
        , pageToButton =
            \page ->
                navigationToPageButton
                    { page = page
                    , mainPageURL = ps.mainPageURL
                    , currentPage = ps.currentPage
                    }
        }


navigationBarWith :
    { navigationPages : List page
    , pageToButton : page -> Html msg
    }
    -> Html msg
navigationBarWith ps =
    nav [ Style.ids.navigation ]
        [ table []
            [ tbody []
                [ tr []
                    (ps.navigationPages
                        |> List.map
                            (\page ->
                                td []
                                    [ ps.pageToButton page
                                    ]
                            )
                    )
                ]
            ]
        ]


pagerButtons :
    { msg : Int -> msg
    , elements : PaginatedList a
    }
    -> Html msg
pagerButtons ps =
    let
        pagerButton pageNum isCurrentPage =
            button
                ([ MaybeUtil.defined <| Style.classes.button.pager
                 , MaybeUtil.defined <| onClick <| ps.msg <| pageNum
                 , MaybeUtil.optional isCurrentPage <| disabled <| True
                 ]
                    |> Maybe.Extra.values
                )
                [ text <| String.fromInt pageNum
                ]

        cells =
            Paginate.elidedPager
                { innerWindow = 5
                , outerWindow = 1
                , pageNumberView = pagerButton
                , gapView = button [ Style.classes.ellipsis ] [ text "..." ]
                }
                ps.elements
                |> List.map (\elt -> td [] [ elt ])
                |> List.filter (Paginate.totalPages ps.elements > 1 |> always)
    in
    table []
        [ tbody []
            [ tr []
                cells
            ]
        ]


paginate :
    { pagination : model -> PaginationSettings
    }
    -> model
    -> List a
    -> PaginatedList a
paginate ps model =
    Paginate.fromList ((ps.pagination >> PaginationSettings.lenses.itemsPerPage.get) model)
        >> Paginate.goTo (model |> (ps.pagination >> PaginationSettings.lenses.currentPage.get))
