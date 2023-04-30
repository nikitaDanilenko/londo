module Ports exposing
    ( deleteToken
    , doDeleteToken
    , doFetchToken
    , fetchToken
    , storeToken
    )


port storeToken : String -> Cmd msg


port doFetchToken : () -> Cmd msg


port fetchToken : (String -> msg) -> Sub msg


port doDeleteToken : () -> Cmd msg


port deleteToken : (() -> msg) -> Sub msg
