module Addresses.ParserUtil exposing
    ( AddressWithParser
    , foldl1
    , nicknameEmailParser
    , uuidParser
    , with1
    , with1Multiple
    , with2
    )

import LondoGQL.Scalar exposing (Uuid(..))
import Url.Parser as Parser exposing ((</>), Parser, s)
import Uuid


uuidParser : Parser (Uuid -> b) b
uuidParser =
    Parser.custom "UUID" Uuid.fromString
        |> Parser.map Uuid.toString
        |> Parser.map Uuid


nicknameEmailParser : AddressWithParser ( String, String ) (String -> String -> a) a
nicknameEmailParser =
    with2
        { step1 = "nickname"
        , toString1 = List.singleton
        , step2 = "email"
        , toString2 = List.singleton
        , paramParser1 = Parser.string
        , paramParser2 = Parser.string
        }


type alias AddressWithParser a i o =
    { address : a -> List String
    , parser : Parser i o
    }


with1 :
    { step1 : String
    , toString : param -> List String
    , paramParser : Parser a b
    }
    -> AddressWithParser param a b
with1 ps =
    { address = \param -> ps.step1 :: ps.toString param
    , parser = s ps.step1 </> ps.paramParser
    }


with1Multiple :
    { steps : List String
    , toString : param -> List String
    , paramParser : Parser a b
    }
    -> AddressWithParser param a b
with1Multiple ps =
    { address = \param -> ps.steps ++ ps.toString param
    , parser =
        case ps.steps of
            [] ->
                ps.paramParser

            step :: steps ->
                foldl1 step steps </> ps.paramParser
    }


with2 :
    { step1 : String
    , toString1 : b -> List String
    , step2 : String
    , toString2 : c -> List String
    , paramParser1 : Parser a1 a2
    , paramParser2 : Parser a2 a3
    }
    -> AddressWithParser ( b, c ) a1 a3
with2 ps =
    { address =
        \( param1, param2 ) ->
            List.singleton ps.step1
                ++ ps.toString1 param1
                ++ List.singleton ps.step2
                ++ ps.toString2 param2
    , parser =
        s ps.step1
            </> ps.paramParser1
            </> s ps.step2
            </> ps.paramParser2
    }


foldl1 : String -> List String -> Parser a a
foldl1 string =
    List.foldl (\str p -> p </> s str) (s string)
