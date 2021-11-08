module Pages.Util.AccessorUtil exposing (everybody, except, nobody, only)

import Graphql.OptionalArgument as OptionalArgument exposing (OptionalArgument)
import List.Nonempty as NE
import LondoGQL.InputObject exposing (AccessorsInput, NonEmptyListOfUserIdInput, UserIdInput)
import LondoGQL.Scalar exposing (Uuid)
import Pages.Util.NonEmptyUtil as NonEmptyUtil


everybody : AccessorsInput
everybody =
    { isAllowList = False
    , userIds = OptionalArgument.Absent
    }


nobody : AccessorsInput
nobody =
    { isAllowList = True
    , userIds = OptionalArgument.Absent
    }


only : NE.Nonempty Uuid -> AccessorsInput
only us =
    { isAllowList = True
    , userIds = usersInputList us
    }


except : NE.Nonempty Uuid -> AccessorsInput
except us =
    { isAllowList = False
    , userIds = usersInputList us
    }


usersInputList : NE.Nonempty Uuid -> OptionalArgument NonEmptyListOfUserIdInput
usersInputList =
    NE.map (\uid -> { uuid = uid })
        >> NonEmptyUtil.nonEmptyToGraphQL
        >> OptionalArgument.Present
