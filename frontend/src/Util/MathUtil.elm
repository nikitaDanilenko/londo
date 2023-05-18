module Util.MathUtil exposing (..)

import Basics.Extra exposing (flip)
import FormatNumber
import FormatNumber.Locales



-- todo: Check usage


numberOfDecimalPlaces : String -> Int
numberOfDecimalPlaces string =
    let
        separator =
            if String.contains "," string then
                Just ","

            else if String.contains "." string then
                Just "."

            else
                Nothing
    in
    separator
        |> Maybe.andThen
            (flip String.split string
                >> List.drop 1
                >> List.head
            )
        |> Maybe.map String.length
        |> Maybe.withDefault 0



-- todo: Check usage


displayFloat : Float -> String
displayFloat =
    FormatNumber.format FormatNumber.Locales.frenchLocale
        >> String.replace "," "."
