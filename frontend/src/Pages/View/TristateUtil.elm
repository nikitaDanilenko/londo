module Pages.View.TristateUtil exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.View.Tristate as Tristate


updateFromSubModel :
    { initialSubModelLens : Lens initial initialSubModel
    , mainSubModelLens : Lens main mainSubModel
    , fromInitToMain : initial -> Maybe main
    , updateSubModel : subModelMsg -> Tristate.Model mainSubModel initialSubModel -> ( Tristate.Model mainSubModel initialSubModel, Cmd subModelMsg )
    , toMsg : subModelMsg -> msg
    }
    -> subModelMsg
    -> Tristate.Model main initial
    -> ( Tristate.Model main initial, Cmd msg )
updateFromSubModel ps msg model =
    let
        ( subModel, subCmd ) =
            model
                |> subModelWith
                    { subInitial = ps.initialSubModelLens.get
                    , subMain = ps.mainSubModelLens.get
                    }
                |> ps.updateSubModel msg

        newCmd =
            Cmd.map ps.toMsg subCmd

        newModel =
            case ( model.status, subModel.status ) of
                ( Tristate.Initial i, Tristate.Initial subInitial ) ->
                    i
                        |> ps.initialSubModelLens.set subInitial
                        |> Tristate.createInitial model.configuration model.errorLanguage
                        |> Tristate.fromInitToMain ps.fromInitToMain

                ( Tristate.Main m, Tristate.Main subMain ) ->
                    m
                        |> ps.mainSubModelLens.set subMain
                        |> Tristate.createMain model.configuration model.errorLanguage

                ( _, Tristate.Error subError ) ->
                    { configuration = model.configuration
                    , status =
                        Tristate.Error
                            { errorExplanation = subError.errorExplanation
                            , previousMain = Tristate.lenses.main.getOption model
                            }
                    , errorLanguage = model.errorLanguage
                    }

                _ ->
                    model
    in
    ( newModel, newCmd )


subModelWith :
    { subInitial : initial -> initialSubModel
    , subMain : main -> mainSubModel
    }
    -> Tristate.Model main initial
    -> Tristate.Model mainSubModel initialSubModel
subModelWith ps model =
    { configuration = model.configuration
    , status =
        Tristate.fold
            { onInitial = ps.subInitial >> Tristate.Initial
            , onMain = ps.subMain >> Tristate.Main
            , onError =
                \es ->
                    Tristate.Error
                        { errorExplanation = es.errorExplanation
                        , previousMain = es.previousMain |> Maybe.map ps.subMain
                        }
            }
            model
    , errorLanguage = model.errorLanguage
    }
