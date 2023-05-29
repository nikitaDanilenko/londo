module Pages.Util.Choice.Handler exposing (updateLogic)

import Basics.Extra exposing (flip)
import Monocle.Compose as Compose
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.Choice.Page
import Pages.Util.Choice.Pagination as Pagination
import Pages.Util.PaginationSettings as PaginationSettings
import Pages.View.Tristate as Tristate
import Result.Extra
import Util.DictList as DictList
import Util.Editing as Editing exposing (Editing)
import Util.LensUtil as LensUtil


updateLogic :
    { idOfElement : element -> elementId
    , idOfUpdate : update -> elementId
    , idOfChoice : choice -> choiceId
    , choiceIdOfElement : element -> choiceId
    , choiceIdOfCreation : creation -> choiceId
    , toUpdate : element -> update
    , toCreation : choice -> parentId -> creation
    , createElement : AuthorizedAccess -> parentId -> creation -> Cmd (Pages.Util.Choice.Page.LogicMsg elementId element update choiceId choice creation)
    , saveElement : AuthorizedAccess -> parentId -> update -> Cmd (Pages.Util.Choice.Page.LogicMsg elementId element update choiceId choice creation)
    , deleteElement : AuthorizedAccess -> parentId -> elementId -> Cmd (Pages.Util.Choice.Page.LogicMsg elementId element update choiceId choice creation)
    , storeChoices : List choice -> Cmd (Pages.Util.Choice.Page.LogicMsg elementId element update choiceId choice creation)
    }
    -> Pages.Util.Choice.Page.LogicMsg elementId element update choiceId choice creation
    -> Pages.Util.Choice.Page.Model parentId elementId element update choiceId choice creation
    -> ( Pages.Util.Choice.Page.Model parentId elementId element update choiceId choice creation, Cmd (Pages.Util.Choice.Page.LogicMsg elementId element update choiceId choice creation) )
updateLogic ps msg model =
    let
        edit update =
            ( model
                |> mapElementStateById (update |> ps.idOfUpdate)
                    (Editing.lenses.update.set update)
            , Cmd.none
            )

        saveEdit elementUpdateClientInput =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (\main ->
                        elementUpdateClientInput
                            |> ps.saveElement
                                { configuration = model.configuration
                                , jwt = main.jwt
                                }
                                main.parentId
                    )
            )

        gotSaveEditResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\element ->
                        model
                            |> mapElementStateById (element |> ps.idOfElement)
                                (Editing.asViewWithElement element)
                    )
            , Cmd.none
            )

        toggleControls elementId =
            ( model
                |> mapElementStateById elementId Editing.toggleControls
            , Cmd.none
            )

        enterEdit elementId =
            ( model
                |> mapElementStateById elementId (Editing.toUpdate ps.toUpdate)
            , Cmd.none
            )

        exitEdit elementId =
            ( model
                |> mapElementStateById elementId Editing.toView
            , Cmd.none
            )

        requestDelete elementId =
            ( model
                |> mapElementStateById elementId Editing.toDelete
            , Cmd.none
            )

        confirmDelete elementId =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (\main ->
                        ps.deleteElement
                            { configuration = model.configuration
                            , jwt = main.jwt
                            }
                            main.parentId
                            elementId
                    )
            )

        cancelDelete elementId =
            ( model
                |> mapElementStateById elementId Editing.toView
            , Cmd.none
            )

        gotDeleteResponse elementId result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (model
                        |> Tristate.mapMain
                            (LensUtil.deleteAtId elementId
                                Pages.Util.Choice.Page.lenses.main.elements
                            )
                        |> always
                    )
            , Cmd.none
            )

        gotFetchElementsResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\elements ->
                        model
                            |> Tristate.mapInitial
                                (Pages.Util.Choice.Page.lenses.initial.elements.set
                                    (elements
                                        |> DictList.fromListWithKey ps.idOfElement
                                        |> Just
                                    )
                                )
                    )
            , Cmd.none
            )

        gotFetchChoicesResponse result =
            result
                |> Result.Extra.unpack (\error -> ( Tristate.toError model error, Cmd.none ))
                    (\choices ->
                        ( model
                            |> Tristate.mapInitial
                                (Pages.Util.Choice.Page.lenses.initial.choices.set
                                    (choices |> DictList.fromListWithKey ps.idOfChoice |> Just)
                                )
                        , choices
                            |> ps.storeChoices
                        )
                    )

        toggleChoiceControls choiceId =
            ( model
                |> Tristate.mapMain (LensUtil.updateById choiceId Pages.Util.Choice.Page.lenses.main.choices Editing.toggleControls)
            , Cmd.none
            )

        selectChoice choice =
            ( model
                |> Tristate.mapMain
                    (\main ->
                        main
                            |> LensUtil.updateById (choice |> ps.idOfChoice)
                                Pages.Util.Choice.Page.lenses.main.choices
                                (Editing.toUpdate (flip ps.toCreation main.parentId))
                    )
            , Cmd.none
            )

        deselectChoice choiceId =
            ( model
                |> Tristate.mapMain
                    (LensUtil.updateById choiceId
                        Pages.Util.Choice.Page.lenses.main.choices
                        Editing.toView
                    )
            , Cmd.none
            )

        create choiceId =
            ( model
            , model
                |> Tristate.lenses.main.getOption
                |> Maybe.andThen
                    (\main ->
                        main
                            |> (Pages.Util.Choice.Page.lenses.main.choices
                                    |> Compose.lensWithOptional (LensUtil.dictByKey choiceId)
                                    |> Compose.optionalWithOptional Editing.lenses.update
                               ).getOption
                            |> Maybe.map
                                (ps.createElement
                                    { configuration = model.configuration
                                    , jwt = main.jwt
                                    }
                                    main.parentId
                                )
                    )
                |> Maybe.withDefault Cmd.none
            )

        gotCreateResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\element ->
                        model
                            |> Tristate.mapMain
                                (LensUtil.insertAtId (element |> ps.idOfElement)
                                    Pages.Util.Choice.Page.lenses.main.elements
                                    (element |> Editing.asView)
                                    >> LensUtil.updateById (element |> ps.choiceIdOfElement)
                                        Pages.Util.Choice.Page.lenses.main.choices
                                        Editing.toView
                                )
                    )
            , Cmd.none
            )

        updateCreation elementCreationClientInput =
            ( model
                |> Tristate.mapMain
                    (LensUtil.updateById (elementCreationClientInput |> ps.choiceIdOfCreation)
                        Pages.Util.Choice.Page.lenses.main.choices
                        (Editing.lenses.update.set elementCreationClientInput)
                    )
            , Cmd.none
            )

        setPagination pagination =
            ( model
                |> Tristate.mapMain (Pages.Util.Choice.Page.lenses.main.pagination.set pagination)
            , Cmd.none
            )

        setElementsSearchString string =
            ( model
                |> Tristate.mapMain
                    (PaginationSettings.setSearchStringAndReset
                        { searchStringLens =
                            Pages.Util.Choice.Page.lenses.main.elementsSearchString
                        , paginationSettingsLens =
                            Pages.Util.Choice.Page.lenses.main.pagination |> Compose.lensWithLens Pagination.lenses.elements
                        }
                        string
                    )
            , Cmd.none
            )

        setChoicesSearchString string =
            ( model
                |> Tristate.mapMain
                    (PaginationSettings.setSearchStringAndReset
                        { searchStringLens =
                            Pages.Util.Choice.Page.lenses.main.choicesSearchString
                        , paginationSettingsLens =
                            Pages.Util.Choice.Page.lenses.main.pagination
                                |> Compose.lensWithLens Pagination.lenses.choices
                        }
                        string
                    )
            , Cmd.none
            )
    in
    case msg of
        Pages.Util.Choice.Page.Edit update ->
            edit update

        Pages.Util.Choice.Page.SaveEdit update ->
            saveEdit update

        Pages.Util.Choice.Page.GotSaveEditResponse result ->
            gotSaveEditResponse result

        Pages.Util.Choice.Page.ToggleControls elementId ->
            toggleControls elementId

        Pages.Util.Choice.Page.EnterEdit elementId ->
            enterEdit elementId

        Pages.Util.Choice.Page.ExitEdit elementId ->
            exitEdit elementId

        Pages.Util.Choice.Page.RequestDelete elementId ->
            requestDelete elementId

        Pages.Util.Choice.Page.ConfirmDelete elementId ->
            confirmDelete elementId

        Pages.Util.Choice.Page.CancelDelete elementId ->
            cancelDelete elementId

        Pages.Util.Choice.Page.GotDeleteResponse elementId result ->
            gotDeleteResponse elementId result

        Pages.Util.Choice.Page.GotFetchElementsResponse result ->
            gotFetchElementsResponse result

        Pages.Util.Choice.Page.GotFetchChoicesResponse result ->
            gotFetchChoicesResponse result

        Pages.Util.Choice.Page.ToggleChoiceControls choiceId ->
            toggleChoiceControls choiceId

        Pages.Util.Choice.Page.SelectChoice choice ->
            selectChoice choice

        Pages.Util.Choice.Page.DeselectChoice choiceId ->
            deselectChoice choiceId

        Pages.Util.Choice.Page.Create choiceId ->
            create choiceId

        Pages.Util.Choice.Page.GotCreateResponse result ->
            gotCreateResponse result

        Pages.Util.Choice.Page.UpdateCreation creation ->
            updateCreation creation

        Pages.Util.Choice.Page.SetPagination pagination ->
            setPagination pagination

        Pages.Util.Choice.Page.SetElementsSearchString string ->
            setElementsSearchString string

        Pages.Util.Choice.Page.SetChoicesSearchString string ->
            setChoicesSearchString string


mapElementStateById :
    elementId
    -> (Editing element update -> Editing element update)
    -> Pages.Util.Choice.Page.Model parentId elementId element update choiceId choice creation
    -> Pages.Util.Choice.Page.Model parentId elementId element update choiceId choice creation
mapElementStateById elementId =
    (Pages.Util.Choice.Page.lenses.main.elements
        |> LensUtil.updateById elementId
    )
        >> Tristate.mapMain
