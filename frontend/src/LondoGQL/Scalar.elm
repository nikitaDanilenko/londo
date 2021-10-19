-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module LondoGQL.Scalar exposing (Codecs, Natural(..), Positive(..), Unit(..), Uuid(..), defaultCodecs, defineCodecs, unwrapCodecs, unwrapEncoder)

import Graphql.Codec exposing (Codec)
import Graphql.Internal.Builder.Object as Object
import Graphql.Internal.Encode
import Json.Decode as Decode exposing (Decoder)
import Json.Encode as Encode


type Natural
    = Natural String


type Positive
    = Positive String


type Unit
    = Unit String


type Uuid
    = Uuid String


defineCodecs :
    { codecNatural : Codec valueNatural
    , codecPositive : Codec valuePositive
    , codecUnit : Codec valueUnit
    , codecUuid : Codec valueUuid
    }
    -> Codecs valueNatural valuePositive valueUnit valueUuid
defineCodecs definitions =
    Codecs definitions


unwrapCodecs :
    Codecs valueNatural valuePositive valueUnit valueUuid
    ->
        { codecNatural : Codec valueNatural
        , codecPositive : Codec valuePositive
        , codecUnit : Codec valueUnit
        , codecUuid : Codec valueUuid
        }
unwrapCodecs (Codecs unwrappedCodecs) =
    unwrappedCodecs


unwrapEncoder :
    (RawCodecs valueNatural valuePositive valueUnit valueUuid -> Codec getterValue)
    -> Codecs valueNatural valuePositive valueUnit valueUuid
    -> getterValue
    -> Graphql.Internal.Encode.Value
unwrapEncoder getter (Codecs unwrappedCodecs) =
    (unwrappedCodecs |> getter |> .encoder) >> Graphql.Internal.Encode.fromJson


type Codecs valueNatural valuePositive valueUnit valueUuid
    = Codecs (RawCodecs valueNatural valuePositive valueUnit valueUuid)


type alias RawCodecs valueNatural valuePositive valueUnit valueUuid =
    { codecNatural : Codec valueNatural
    , codecPositive : Codec valuePositive
    , codecUnit : Codec valueUnit
    , codecUuid : Codec valueUuid
    }


defaultCodecs : RawCodecs Natural Positive Unit Uuid
defaultCodecs =
    { codecNatural =
        { encoder = \(Natural raw) -> Encode.string raw
        , decoder = Object.scalarDecoder |> Decode.map Natural
        }
    , codecPositive =
        { encoder = \(Positive raw) -> Encode.string raw
        , decoder = Object.scalarDecoder |> Decode.map Positive
        }
    , codecUnit =
        { encoder = \(Unit raw) -> Encode.string raw
        , decoder = Object.scalarDecoder |> Decode.map Unit
        }
    , codecUuid =
        { encoder = \(Uuid raw) -> Encode.string raw
        , decoder = Object.scalarDecoder |> Decode.map Uuid
        }
    }
