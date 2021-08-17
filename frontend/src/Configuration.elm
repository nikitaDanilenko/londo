module Configuration exposing (..)

--todo: Keep watch for possible restructuring; this may be sensible to provide only the relevant configuration flags
type alias Configuration =
    { graphQLEndpoint : String
    , mainPageURL : String
    }
