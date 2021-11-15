module Pages.Util.FromInput exposing (..)

import List.Extra
import LondoGQL.Scalar exposing (Natural, Positive)
import Maybe.Extra
import Monocle.Lens exposing (Lens)
import Pages.Util.ScalarUtil as ScalarUtil


type alias FromInput a =
    { value : a
    , ifEmptyValue : a
    , text : String
    , parse : String -> Result String a
    , partial : String -> Bool
    }


text : Lens (FromInput a) String
text =
    Lens .text (\b a -> { a | text = b })


value : Lens (FromInput a) a
value =
    Lens .value (\b a -> { a | value = b })


emptyText :
    { ifEmptyValue : a
    , value : a
    , parse : String -> Result String a
    , isPartial : String -> Bool
    }
    -> FromInput a
emptyText params =
    { value = params.value
    , ifEmptyValue = params.ifEmptyValue
    , text = ""
    , parse = params.parse
    , partial = params.isPartial
    }


isValid : FromInput a -> Bool
isValid fromInput =
    case fromInput.parse fromInput.text of
        Ok v ->
            v == fromInput.value

        Err _ ->
            False


lift : (model -> FromInput a -> model) -> FromInput a -> String -> model -> model
lift ui fromInput txt model =
    let
        possiblyValid =
            if String.isEmpty txt || fromInput.partial txt then
                fromInput
                    |> value.set fromInput.ifEmptyValue
                    |> text.set txt

            else
                fromInput
    in
    case fromInput.parse txt of
        Ok v ->
            possiblyValid
                |> value.set v
                |> ui model

        Err _ ->
            ui model possiblyValid


natural : FromInput Natural
natural =
    let
        zero =
            Natural "0"

        parseNatural : String -> Result String Natural
        parseNatural str =
            (if String.isEmpty str then
                zero
                    |> ScalarUtil.naturalToString
                    |> Ok

             else if String.all Char.isDigit str then
                Ok str

             else
                Err "The string does not represent a natural number"
            )
                |> Result.map
                    (String.toList
                        >> List.Extra.dropWhile ((==) '0')
                        >> String.fromList
                        >> Natural
                    )
    in
    emptyText
        { value = zero
        , ifEmptyValue = zero
        , parse = parseNatural
        , isPartial =
            parseNatural
                >> Result.toMaybe
                >> Maybe.Extra.isJust
        }

positive : FromInput Positive
positive =
    let
        one =
            Positive "1"

        parsePositive : String -> Result String Positive
        parsePositive str =
            (if String.isEmpty str then
                one
                    |> ScalarUtil.positiveToString
                    |> Ok

             else if String.all Char.isDigit str && String.any ((/=) '0') str then
                Ok str

             else
                Err "The string does not represent a natural number"
            )
                |> Result.map
                    (String.toList
                        >> List.Extra.dropWhile ((==) '0')
                        >> String.fromList
                        >> Positive
                    )
    in
    emptyText
        { value = one
        , ifEmptyValue = one
        , parse = parsePositive
        , isPartial =
            parsePositive
                >> Result.toMaybe
                >> Maybe.Extra.isJust
        }