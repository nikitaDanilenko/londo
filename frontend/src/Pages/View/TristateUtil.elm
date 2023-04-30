module Pages.View.TristateUtil exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.View.Tristate as Tristate


updateFromSubModel :
    { initialSubModelLens : Lens initial initialSubModel
    , mainSubModelLens : Lens main mainSubModel
    , subModelOf : Tristate.Model main initial -> Tristate.Model mainSubModel initialSubModel
    , fromInitToMain : initial -> Maybe main
    , updateSubModel : subModelMsg -> Tristate.Model mainSubModel initialSubModel -> ( Tristate.Model mainSubModel initialSubModel, Cmd subModelMsg )
    , toMsg : subModelMsg -> msg
    }
    -> subModelMsg
    -> Tristate.Model main initial
    -> ( Tristate.Model main initial, Cmd msg )
updateFromSubModel ps msg model =
    let
        ( recipeModel, recipeCmd ) =
            ps.updateSubModel msg (model |> ps.subModelOf)

        newCmd =
            Cmd.map ps.toMsg recipeCmd

        newModel =
            case ( model.status, recipeModel.status ) of
                ( Tristate.Initial i, Tristate.Initial subModel ) ->
                    i
                        |> ps.initialSubModelLens.set subModel
                        |> Tristate.createInitial model.configuration
                        |> Tristate.fromInitToMain ps.fromInitToMain

                ( Tristate.Main m, Tristate.Main subModel ) ->
                    m
                        |> ps.mainSubModelLens.set subModel
                        |> Tristate.createMain model.configuration

                ( _, Tristate.Error subModel ) ->
                    { configuration = model.configuration
                    , status =
                        Tristate.Error
                            { errorExplanation = subModel.errorExplanation
                            , previousMain = Tristate.lenses.main.getOption model
                            }
                    }

                _ ->
                    model
    in
    ( newModel, newCmd )


subModelWith :
    { initialLens : Lens initial initialSubModel
    , mainLens : Lens main mainSubModel
    }
    -> Tristate.Model main initial
    -> Tristate.Model mainSubModel initialSubModel
subModelWith ps model =
    { configuration = model.configuration
    , status =
        Tristate.fold
            { onInitial = ps.initialLens.get >> Tristate.Initial
            , onMain = ps.mainLens.get >> Tristate.Main
            , onError =
                \es ->
                    Tristate.Error
                        { errorExplanation = es.errorExplanation
                        , previousMain = es.previousMain |> Maybe.map ps.mainLens.get
                        }
            }
            model
    }
