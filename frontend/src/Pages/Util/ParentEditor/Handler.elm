module Pages.Util.ParentEditor.Handler exposing (updateLogic)

import Maybe.Extra
import Monocle.Compose as Compose
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.Links as Links
import Pages.Util.PaginationSettings as PaginationSettings
import Pages.Util.ParentEditor.Page as Page
import Pages.Util.ParentEditor.Pagination as Pagination
import Pages.View.Tristate as Tristate
import Result.Extra
import Util.DictList as DictList
import Util.Editing as Editing exposing (Editing)
import Util.LensUtil as LensUtil


updateLogic :
    { idOfParent : parent -> parentId
    , toUpdate : parent -> update
    , navigateToAddress : parentId -> Maybe (List String)
    , create : AuthorizedAccess -> creation -> Cmd (Page.LogicMsg parentId parent creation update)
    , save : AuthorizedAccess -> parentId -> update -> Cmd (Page.LogicMsg parentId parent creation update)
    , delete : AuthorizedAccess -> parentId -> Cmd (Page.LogicMsg parentId parent creation update)
    }
    -> Page.LogicMsg parentId parent creation update
    -> Page.Model parentId parent creation update language
    -> ( Page.Model parentId parent creation update language, Cmd (Page.LogicMsg parentId parent creation update) )
updateLogic ps msg model =
    let
        gotFetchResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\parents ->
                        model
                            |> Tristate.mapInitial
                                (Page.lenses.initial.parents.set
                                    (parents
                                        |> List.map Editing.asView
                                        |> DictList.fromListWithKey (.original >> ps.idOfParent)
                                        |> Just
                                    )
                                )
                            |> Tristate.fromInitToMain Page.initialToMain
                    )
            , Cmd.none
            )

        updateCreation creation =
            ( model
                |> Tristate.mapMain (Page.lenses.main.parentCreation.set creation)
            , Cmd.none
            )

        create =
            ( model
            , model
                |> Tristate.lenses.main.getOption
                |> Maybe.andThen
                    (\main ->
                        main.parentCreation
                            |> Maybe.map
                                (ps.create
                                    { configuration = model.configuration
                                    , jwt = main.jwt
                                    }
                                )
                    )
                |> Maybe.withDefault Cmd.none
            )

        gotCreationResponseWith params result =
            result
                |> Result.Extra.unpack (\error -> ( Tristate.toError model error, Cmd.none ))
                    (\parent ->
                        let
                            parentId =
                                parent |> ps.idOfParent

                            parentCreationHandling =
                                if params.resetParentCreation then
                                    Page.lenses.main.parentCreation.set Nothing

                                else
                                    identity
                        in
                        ( model
                            |> Tristate.mapMain
                                (LensUtil.insertAtId parentId
                                    Page.lenses.main.parents
                                    (parent |> Editing.asView)
                                    >> parentCreationHandling
                                )
                        , parentId
                            |> ps.navigateToAddress
                            |> Maybe.Extra.unwrap
                                Cmd.none
                                (Links.loadFrontendPage model.configuration)
                        )
                    )

        gotCreateResponse =
            gotCreationResponseWith { resetParentCreation = True }

        edit parentId update =
            ( model
                |> mapParentStateById parentId
                    (Editing.lenses.update.set update)
            , Cmd.none
            )

        saveEdit parentId =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (\main ->
                        main
                            |> Page.lenses.main.parents.get
                            |> DictList.get parentId
                            |> Maybe.andThen Editing.extractUpdate
                            |> Maybe.Extra.unwrap
                                Cmd.none
                                (ps.save
                                    { configuration = model.configuration
                                    , jwt = main.jwt
                                    }
                                    parentId
                                )
                    )
            )

        gotSaveEditResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\parent ->
                        model
                            |> mapParentStateById (parent |> ps.idOfParent)
                                (Editing.asViewWithElement parent)
                    )
            , Cmd.none
            )

        toggleControls parentId =
            ( model
                |> mapParentStateById parentId Editing.toggleControls
            , Cmd.none
            )

        enterEdit parentId =
            ( model
                |> mapParentStateById parentId (Editing.toUpdate ps.toUpdate)
            , Cmd.none
            )

        exitEdit parentId =
            ( model |> mapParentStateById parentId Editing.toView
            , Cmd.none
            )

        requestDelete parentId =
            ( model |> mapParentStateById parentId Editing.toDelete
            , Cmd.none
            )

        confirmDelete parentId =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (\main ->
                        ps.delete
                            { configuration = model.configuration
                            , jwt = main.jwt
                            }
                            parentId
                    )
            )

        cancelDelete parentId =
            ( model |> mapParentStateById parentId Editing.toView
            , Cmd.none
            )

        gotDeleteResponse deletedId result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (always
                        (model
                            |> Tristate.mapMain (LensUtil.deleteAtId deletedId Page.lenses.main.parents)
                        )
                    )
            , Cmd.none
            )

        setPagination pagination =
            ( model
                |> Tristate.mapMain (Page.lenses.main.pagination.set pagination)
            , Cmd.none
            )

        setSearchString string =
            ( model
                |> Tristate.mapMain
                    (PaginationSettings.setSearchStringAndReset
                        { searchStringLens =
                            Page.lenses.main.searchString
                        , paginationSettingsLens =
                            Page.lenses.main.pagination
                                |> Compose.lensWithLens Pagination.lenses.parents
                        }
                        string
                    )
            , Cmd.none
            )
    in
    case msg of
        Page.UpdateCreation creation ->
            updateCreation creation

        Page.Create ->
            create

        Page.GotCreateResponse result ->
            gotCreateResponse result

        Page.Edit parentId update ->
            edit parentId update

        Page.SaveEdit parentId ->
            saveEdit parentId

        Page.GotSaveEditResponse result ->
            gotSaveEditResponse result

        Page.ToggleControls parentId ->
            toggleControls parentId

        Page.EnterEdit parentId ->
            enterEdit parentId

        Page.ExitEdit parentId ->
            exitEdit parentId

        Page.RequestDelete parentId ->
            requestDelete parentId

        Page.ConfirmDelete parentId ->
            confirmDelete parentId

        Page.CancelDelete parentId ->
            cancelDelete parentId

        Page.GotDeleteResponse parentId result ->
            gotDeleteResponse parentId result

        Page.GotFetchResponse result ->
            gotFetchResponse result

        Page.SetPagination pagination ->
            setPagination pagination

        Page.SetSearchString string ->
            setSearchString string


mapParentStateById :
    parentId
    -> (Editing parent update -> Editing parent update)
    -> Page.Model parentId parent creation update language
    -> Page.Model parentId parent creation update language
mapParentStateById parentId =
    LensUtil.updateById parentId Page.lenses.main.parents
        >> Tristate.mapMain
