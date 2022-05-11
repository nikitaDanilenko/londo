module Pages.Util.ParserUtil exposing (..)

import List.Extra
import LondoGQL.Scalar exposing (Uuid(..))
import Url.Parser as Parser exposing (Parser)


uuidParser : Parser (Uuid -> b) b
uuidParser =
    Parser.custom "UUID" matchUuid



-- todo: Version 4 UUID have restrictions on two positions - implement these


matchUuid : String -> Maybe Uuid
matchUuid str =
    let
        gs =
            splitAt '-' (String.toList str)

        groupsCorrect =
            -- UUID format
            List.map List.length gs == [ 8, 4, 4, 4, 12 ]

        symbolsCorrect =
            -- UUID character set
            List.all Char.isHexDigit (List.concat gs)
    in
    if groupsCorrect && symbolsCorrect then
        Just (Uuid str)

    else
        Nothing



-- todo: Move splitAt to a more sensible position


splitAt : a -> List a -> List (List a)
splitAt sep xs =
    case xs of
        [] ->
            []

        l ->
            let
                ( start, end ) =
                    List.Extra.break ((==) sep) l
            in
            start :: splitAt sep (List.drop 1 end)
