module Util.SearchUtil exposing (search, searchByInitials, searchByWords)

import Basics.Extra exposing (flip)
import List.Extra
import Maybe.Extra


type alias SearchParameters =
    { desired : String
    , actual : String
    }


searchByWords : SearchParameters -> Bool
searchByWords ps =
    ps.desired
        |> String.toLower
        |> String.split " "
        |> List.all (ps.actual |> String.toLower |> flip String.contains)


searchByInitials : SearchParameters -> Bool
searchByInitials ps =
    ps.actual
        |> String.toLower
        |> String.split " "
        |> List.concatMap (String.split "-")
        |> List.Extra.andThen (String.toList >> List.Extra.dropWhile (Char.isAlpha >> not) >> List.head >> Maybe.Extra.toList)
        |> List.Extra.isSubsequenceOf (ps.desired |> String.toLower |> String.toList)


search : String -> String -> Bool
search desired actual =
    let
        searchParameters =
            { desired = desired
            , actual = actual
            }
    in
    List.any identity [ searchByWords searchParameters, searchByInitials searchParameters ]
