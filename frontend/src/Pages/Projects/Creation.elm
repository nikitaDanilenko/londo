module Pages.Projects.Creation exposing (..)

import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.InputObject exposing (CreateProjectInput)
import Monocle.Lens exposing (Lens)
import Pages.Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


type alias ClientInput =
    { name : ValidatedInput String
    , description : Maybe String
    }


default : ClientInput
default =
    { name = ValidatedInput.nonEmptyString
    , description = Nothing
    }


lenses :
    { name : Lens ClientInput (ValidatedInput String)
    , description : Lens ClientInput (Maybe String)
    }
lenses =
    { name = Lens .name (\b a -> { a | name = b })
    , description = Lens .description (\b a -> { a | description = b })
    }


toCreation : ClientInput -> LondoGQL.InputObject.CreateProjectInput
toCreation input =
    { name = input.name.value
    , description = input.description |> OptionalArgument.fromMaybe
    }
