-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module LondoGQL.Scalar exposing (Codecs, Unit(..), Uuid(..), defaultCodecs, defineCodecs, unwrapCodecs, unwrapEncoder)

import Graphql.Codec exposing (Codec)
import Graphql.Internal.Builder.Object as Object
import Graphql.Internal.Encode
import Json.Decode as Decode exposing (Decoder)
import Json.Encode as Encode


type Unit
    = Unit String


type Uuid
    = Uuid String


defineCodecs :
    { codecUnit : Codec valueUnit
    , codecUuid : Codec valueUuid
    }
    -> Codecs valueUnit valueUuid
defineCodecs definitions =
    Codecs definitions


unwrapCodecs :
    Codecs valueUnit valueUuid
    ->
        { codecUnit : Codec valueUnit
        , codecUuid : Codec valueUuid
        }
unwrapCodecs (Codecs unwrappedCodecs) =
    unwrappedCodecs


unwrapEncoder :
    (RawCodecs valueUnit valueUuid -> Codec getterValue)
    -> Codecs valueUnit valueUuid
    -> getterValue
    -> Graphql.Internal.Encode.Value
unwrapEncoder getter (Codecs unwrappedCodecs) =
    (unwrappedCodecs |> getter |> .encoder) >> Graphql.Internal.Encode.fromJson


type Codecs valueUnit valueUuid
    = Codecs (RawCodecs valueUnit valueUuid)


type alias RawCodecs valueUnit valueUuid =
    { codecUnit : Codec valueUnit
    , codecUuid : Codec valueUuid
    }


defaultCodecs : RawCodecs Unit Uuid
defaultCodecs =
    { codecUnit =
        { encoder = \(Unit raw) -> Encode.string raw
        , decoder = Object.scalarDecoder |> Decode.map Unit
        }
    , codecUuid =
        { encoder = \(Uuid raw) -> Encode.string raw
        , decoder = Object.scalarDecoder |> Decode.map Uuid
        }
    }
