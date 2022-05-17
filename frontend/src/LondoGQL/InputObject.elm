-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module LondoGQL.InputObject exposing (..)

import Graphql.Internal.Builder.Argument as Argument exposing (Argument)
import Graphql.Internal.Builder.Object as Object
import Graphql.Internal.Encode as Encode exposing (Value)
import Graphql.OptionalArgument exposing (OptionalArgument(..))
import Graphql.SelectionSet exposing (SelectionSet)
import Json.Decode as Decode
import LondoGQL.Enum.TaskKind
import LondoGQL.Interface
import LondoGQL.Object
import LondoGQL.Scalar
import LondoGQL.ScalarCodecs
import LondoGQL.Union


buildAccessorsInput :
    AccessorsInputRequiredFields
    -> (AccessorsInputOptionalFields -> AccessorsInputOptionalFields)
    -> AccessorsInput
buildAccessorsInput required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { userIds = Absent }
    in
    { isAllowList = required____.isAllowList, userIds = optionals____.userIds }


type alias AccessorsInputRequiredFields =
    { isAllowList : Bool }


type alias AccessorsInputOptionalFields =
    { userIds : OptionalArgument NonEmptyListOfUserIdInput }


{-| Type for the AccessorsInput input object.
-}
type alias AccessorsInput =
    { isAllowList : Bool
    , userIds : OptionalArgument NonEmptyListOfUserIdInput
    }


{-| Encode a AccessorsInput into a value that can be used as an argument.
-}
encodeAccessorsInput : AccessorsInput -> Value
encodeAccessorsInput input____ =
    Encode.maybeObject
        [ ( "isAllowList", Encode.bool input____.isAllowList |> Just ), ( "userIds", encodeNonEmptyListOfUserIdInput |> Encode.optional input____.userIds ) ]


buildDashboardCreation :
    DashboardCreationRequiredFields
    -> (DashboardCreationOptionalFields -> DashboardCreationOptionalFields)
    -> DashboardCreation
buildDashboardCreation required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { description = Absent }
    in
    { header = required____.header, description = optionals____.description, readAccessors = required____.readAccessors, writeAccessors = required____.writeAccessors }


type alias DashboardCreationRequiredFields =
    { header : String
    , readAccessors : AccessorsInput
    , writeAccessors : AccessorsInput
    }


type alias DashboardCreationOptionalFields =
    { description : OptionalArgument String }


{-| Type for the DashboardCreation input object.
-}
type alias DashboardCreation =
    { header : String
    , description : OptionalArgument String
    , readAccessors : AccessorsInput
    , writeAccessors : AccessorsInput
    }


{-| Encode a DashboardCreation into a value that can be used as an argument.
-}
encodeDashboardCreation : DashboardCreation -> Value
encodeDashboardCreation input____ =
    Encode.maybeObject
        [ ( "header", Encode.string input____.header |> Just ), ( "description", Encode.string |> Encode.optional input____.description ), ( "readAccessors", encodeAccessorsInput input____.readAccessors |> Just ), ( "writeAccessors", encodeAccessorsInput input____.writeAccessors |> Just ) ]


buildDashboardIdInput :
    DashboardIdInputRequiredFields
    -> DashboardIdInput
buildDashboardIdInput required____ =
    { uuid = required____.uuid }


type alias DashboardIdInputRequiredFields =
    { uuid : LondoGQL.ScalarCodecs.Uuid }


{-| Type for the DashboardIdInput input object.
-}
type alias DashboardIdInput =
    { uuid : LondoGQL.ScalarCodecs.Uuid }


{-| Encode a DashboardIdInput into a value that can be used as an argument.
-}
encodeDashboardIdInput : DashboardIdInput -> Value
encodeDashboardIdInput input____ =
    Encode.maybeObject
        [ ( "uuid", (LondoGQL.ScalarCodecs.codecs |> LondoGQL.Scalar.unwrapEncoder .codecUuid) input____.uuid |> Just ) ]


buildDashboardUpdate :
    DashboardUpdateRequiredFields
    -> (DashboardUpdateOptionalFields -> DashboardUpdateOptionalFields)
    -> DashboardUpdate
buildDashboardUpdate required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { description = Absent }
    in
    { header = required____.header, description = optionals____.description, userId = required____.userId, flatIfSingleTask = required____.flatIfSingleTask }


type alias DashboardUpdateRequiredFields =
    { header : String
    , userId : UserIdInput
    , flatIfSingleTask : Bool
    }


type alias DashboardUpdateOptionalFields =
    { description : OptionalArgument String }


{-| Type for the DashboardUpdate input object.
-}
type alias DashboardUpdate =
    { header : String
    , description : OptionalArgument String
    , userId : UserIdInput
    , flatIfSingleTask : Bool
    }


{-| Encode a DashboardUpdate into a value that can be used as an argument.
-}
encodeDashboardUpdate : DashboardUpdate -> Value
encodeDashboardUpdate input____ =
    Encode.maybeObject
        [ ( "header", Encode.string input____.header |> Just ), ( "description", Encode.string |> Encode.optional input____.description ), ( "userId", encodeUserIdInput input____.userId |> Just ), ( "flatIfSingleTask", Encode.bool input____.flatIfSingleTask |> Just ) ]


buildNaturalInput :
    NaturalInputRequiredFields
    -> NaturalInput
buildNaturalInput required____ =
    { nonNegative = required____.nonNegative }


type alias NaturalInputRequiredFields =
    { nonNegative : Int }


{-| Type for the NaturalInput input object.
-}
type alias NaturalInput =
    { nonNegative : Int }


{-| Encode a NaturalInput into a value that can be used as an argument.
-}
encodeNaturalInput : NaturalInput -> Value
encodeNaturalInput input____ =
    Encode.maybeObject
        [ ( "nonNegative", Encode.int input____.nonNegative |> Just ) ]


buildNonEmptyListOfUserIdInput :
    NonEmptyListOfUserIdInputRequiredFields
    -> NonEmptyListOfUserIdInput
buildNonEmptyListOfUserIdInput required____ =
    { head = required____.head, tail = required____.tail }


type alias NonEmptyListOfUserIdInputRequiredFields =
    { head : UserIdInput
    , tail : List UserIdInput
    }


{-| Type for the NonEmptyListOfUserIdInput input object.
-}
type alias NonEmptyListOfUserIdInput =
    { head : UserIdInput
    , tail : List UserIdInput
    }


{-| Encode a NonEmptyListOfUserIdInput into a value that can be used as an argument.
-}
encodeNonEmptyListOfUserIdInput : NonEmptyListOfUserIdInput -> Value
encodeNonEmptyListOfUserIdInput input____ =
    Encode.maybeObject
        [ ( "head", encodeUserIdInput input____.head |> Just ), ( "tail", (encodeUserIdInput |> Encode.list) input____.tail |> Just ) ]


buildPlainCreation :
    PlainCreationRequiredFields
    -> (PlainCreationOptionalFields -> PlainCreationOptionalFields)
    -> PlainCreation
buildPlainCreation required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { unit = Absent }
    in
    { name = required____.name, taskKind = required____.taskKind, unit = optionals____.unit, progress = required____.progress, weight = required____.weight }


type alias PlainCreationRequiredFields =
    { name : String
    , taskKind : LondoGQL.Enum.TaskKind.TaskKind
    , progress : ProgressInput
    , weight : PositiveInput
    }


type alias PlainCreationOptionalFields =
    { unit : OptionalArgument String }


{-| Type for the PlainCreation input object.
-}
type alias PlainCreation =
    { name : String
    , taskKind : LondoGQL.Enum.TaskKind.TaskKind
    , unit : OptionalArgument String
    , progress : ProgressInput
    , weight : PositiveInput
    }


{-| Encode a PlainCreation into a value that can be used as an argument.
-}
encodePlainCreation : PlainCreation -> Value
encodePlainCreation input____ =
    Encode.maybeObject
        [ ( "name", Encode.string input____.name |> Just ), ( "taskKind", Encode.enum LondoGQL.Enum.TaskKind.toString input____.taskKind |> Just ), ( "unit", Encode.string |> Encode.optional input____.unit ), ( "progress", encodeProgressInput input____.progress |> Just ), ( "weight", encodePositiveInput input____.weight |> Just ) ]


buildPlainUpdate :
    PlainUpdateRequiredFields
    -> (PlainUpdateOptionalFields -> PlainUpdateOptionalFields)
    -> PlainUpdate
buildPlainUpdate required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { unit = Absent }
    in
    { name = required____.name, taskKind = required____.taskKind, unit = optionals____.unit, weight = required____.weight, progressUpdate = required____.progressUpdate }


type alias PlainUpdateRequiredFields =
    { name : String
    , taskKind : LondoGQL.Enum.TaskKind.TaskKind
    , weight : PositiveInput
    , progressUpdate : ProgressUpdate
    }


type alias PlainUpdateOptionalFields =
    { unit : OptionalArgument String }


{-| Type for the PlainUpdate input object.
-}
type alias PlainUpdate =
    { name : String
    , taskKind : LondoGQL.Enum.TaskKind.TaskKind
    , unit : OptionalArgument String
    , weight : PositiveInput
    , progressUpdate : ProgressUpdate
    }


{-| Encode a PlainUpdate into a value that can be used as an argument.
-}
encodePlainUpdate : PlainUpdate -> Value
encodePlainUpdate input____ =
    Encode.maybeObject
        [ ( "name", Encode.string input____.name |> Just ), ( "taskKind", Encode.enum LondoGQL.Enum.TaskKind.toString input____.taskKind |> Just ), ( "unit", Encode.string |> Encode.optional input____.unit ), ( "weight", encodePositiveInput input____.weight |> Just ), ( "progressUpdate", encodeProgressUpdate input____.progressUpdate |> Just ) ]


buildPositiveInput :
    PositiveInputRequiredFields
    -> PositiveInput
buildPositiveInput required____ =
    { positive = required____.positive }


type alias PositiveInputRequiredFields =
    { positive : Int }


{-| Type for the PositiveInput input object.
-}
type alias PositiveInput =
    { positive : Int }


{-| Encode a PositiveInput into a value that can be used as an argument.
-}
encodePositiveInput : PositiveInput -> Value
encodePositiveInput input____ =
    Encode.maybeObject
        [ ( "positive", Encode.int input____.positive |> Just ) ]


buildProgressInput :
    ProgressInputRequiredFields
    -> ProgressInput
buildProgressInput required____ =
    { reached = required____.reached, reachable = required____.reachable }


type alias ProgressInputRequiredFields =
    { reached : NaturalInput
    , reachable : PositiveInput
    }


{-| Type for the ProgressInput input object.
-}
type alias ProgressInput =
    { reached : NaturalInput
    , reachable : PositiveInput
    }


{-| Encode a ProgressInput into a value that can be used as an argument.
-}
encodeProgressInput : ProgressInput -> Value
encodeProgressInput input____ =
    Encode.maybeObject
        [ ( "reached", encodeNaturalInput input____.reached |> Just ), ( "reachable", encodePositiveInput input____.reachable |> Just ) ]


buildProgressUpdate :
    ProgressUpdateRequiredFields
    -> ProgressUpdate
buildProgressUpdate required____ =
    { reached = required____.reached, reachable = required____.reachable }


type alias ProgressUpdateRequiredFields =
    { reached : NaturalInput
    , reachable : PositiveInput
    }


{-| Type for the ProgressUpdate input object.
-}
type alias ProgressUpdate =
    { reached : NaturalInput
    , reachable : PositiveInput
    }


{-| Encode a ProgressUpdate into a value that can be used as an argument.
-}
encodeProgressUpdate : ProgressUpdate -> Value
encodeProgressUpdate input____ =
    Encode.maybeObject
        [ ( "reached", encodeNaturalInput input____.reached |> Just ), ( "reachable", encodePositiveInput input____.reachable |> Just ) ]


buildProjectCreation :
    ProjectCreationRequiredFields
    -> (ProjectCreationOptionalFields -> ProjectCreationOptionalFields)
    -> ProjectCreation
buildProjectCreation required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { description = Absent }
    in
    { name = required____.name, description = optionals____.description, flatIfSingleTask = required____.flatIfSingleTask, readAccessors = required____.readAccessors, writeAccessors = required____.writeAccessors }


type alias ProjectCreationRequiredFields =
    { name : String
    , flatIfSingleTask : Bool
    , readAccessors : AccessorsInput
    , writeAccessors : AccessorsInput
    }


type alias ProjectCreationOptionalFields =
    { description : OptionalArgument String }


{-| Type for the ProjectCreation input object.
-}
type alias ProjectCreation =
    { name : String
    , description : OptionalArgument String
    , flatIfSingleTask : Bool
    , readAccessors : AccessorsInput
    , writeAccessors : AccessorsInput
    }


{-| Encode a ProjectCreation into a value that can be used as an argument.
-}
encodeProjectCreation : ProjectCreation -> Value
encodeProjectCreation input____ =
    Encode.maybeObject
        [ ( "name", Encode.string input____.name |> Just ), ( "description", Encode.string |> Encode.optional input____.description ), ( "flatIfSingleTask", Encode.bool input____.flatIfSingleTask |> Just ), ( "readAccessors", encodeAccessorsInput input____.readAccessors |> Just ), ( "writeAccessors", encodeAccessorsInput input____.writeAccessors |> Just ) ]


buildProjectIdInput :
    ProjectIdInputRequiredFields
    -> ProjectIdInput
buildProjectIdInput required____ =
    { uuid = required____.uuid }


type alias ProjectIdInputRequiredFields =
    { uuid : LondoGQL.ScalarCodecs.Uuid }


{-| Type for the ProjectIdInput input object.
-}
type alias ProjectIdInput =
    { uuid : LondoGQL.ScalarCodecs.Uuid }


{-| Encode a ProjectIdInput into a value that can be used as an argument.
-}
encodeProjectIdInput : ProjectIdInput -> Value
encodeProjectIdInput input____ =
    Encode.maybeObject
        [ ( "uuid", (LondoGQL.ScalarCodecs.codecs |> LondoGQL.Scalar.unwrapEncoder .codecUuid) input____.uuid |> Just ) ]


buildProjectReferenceCreation :
    ProjectReferenceCreationRequiredFields
    -> ProjectReferenceCreation
buildProjectReferenceCreation required____ =
    { weight = required____.weight, projectReferenceId = required____.projectReferenceId }


type alias ProjectReferenceCreationRequiredFields =
    { weight : PositiveInput
    , projectReferenceId : ProjectIdInput
    }


{-| Type for the ProjectReferenceCreation input object.
-}
type alias ProjectReferenceCreation =
    { weight : PositiveInput
    , projectReferenceId : ProjectIdInput
    }


{-| Encode a ProjectReferenceCreation into a value that can be used as an argument.
-}
encodeProjectReferenceCreation : ProjectReferenceCreation -> Value
encodeProjectReferenceCreation input____ =
    Encode.maybeObject
        [ ( "weight", encodePositiveInput input____.weight |> Just ), ( "projectReferenceId", encodeProjectIdInput input____.projectReferenceId |> Just ) ]


buildProjectReferenceUpdate :
    ProjectReferenceUpdateRequiredFields
    -> ProjectReferenceUpdate
buildProjectReferenceUpdate required____ =
    { projectReferenceId = required____.projectReferenceId, weight = required____.weight }


type alias ProjectReferenceUpdateRequiredFields =
    { projectReferenceId : ProjectIdInput
    , weight : PositiveInput
    }


{-| Type for the ProjectReferenceUpdate input object.
-}
type alias ProjectReferenceUpdate =
    { projectReferenceId : ProjectIdInput
    , weight : PositiveInput
    }


{-| Encode a ProjectReferenceUpdate into a value that can be used as an argument.
-}
encodeProjectReferenceUpdate : ProjectReferenceUpdate -> Value
encodeProjectReferenceUpdate input____ =
    Encode.maybeObject
        [ ( "projectReferenceId", encodeProjectIdInput input____.projectReferenceId |> Just ), ( "weight", encodePositiveInput input____.weight |> Just ) ]


buildProjectUpdate :
    ProjectUpdateRequiredFields
    -> (ProjectUpdateOptionalFields -> ProjectUpdateOptionalFields)
    -> ProjectUpdate
buildProjectUpdate required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { description = Absent }
    in
    { name = required____.name, description = optionals____.description, ownerId = required____.ownerId, flatIfSingleTask = required____.flatIfSingleTask }


type alias ProjectUpdateRequiredFields =
    { name : String
    , ownerId : UserIdInput
    , flatIfSingleTask : Bool
    }


type alias ProjectUpdateOptionalFields =
    { description : OptionalArgument String }


{-| Type for the ProjectUpdate input object.
-}
type alias ProjectUpdate =
    { name : String
    , description : OptionalArgument String
    , ownerId : UserIdInput
    , flatIfSingleTask : Bool
    }


{-| Encode a ProjectUpdate into a value that can be used as an argument.
-}
encodeProjectUpdate : ProjectUpdate -> Value
encodeProjectUpdate input____ =
    Encode.maybeObject
        [ ( "name", Encode.string input____.name |> Just ), ( "description", Encode.string |> Encode.optional input____.description ), ( "ownerId", encodeUserIdInput input____.ownerId |> Just ), ( "flatIfSingleTask", Encode.bool input____.flatIfSingleTask |> Just ) ]


buildProjectWeightOnDashboardInput :
    ProjectWeightOnDashboardInputRequiredFields
    -> ProjectWeightOnDashboardInput
buildProjectWeightOnDashboardInput required____ =
    { projectId = required____.projectId, weight = required____.weight }


type alias ProjectWeightOnDashboardInputRequiredFields =
    { projectId : ProjectIdInput
    , weight : NaturalInput
    }


{-| Type for the ProjectWeightOnDashboardInput input object.
-}
type alias ProjectWeightOnDashboardInput =
    { projectId : ProjectIdInput
    , weight : NaturalInput
    }


{-| Encode a ProjectWeightOnDashboardInput into a value that can be used as an argument.
-}
encodeProjectWeightOnDashboardInput : ProjectWeightOnDashboardInput -> Value
encodeProjectWeightOnDashboardInput input____ =
    Encode.maybeObject
        [ ( "projectId", encodeProjectIdInput input____.projectId |> Just ), ( "weight", encodeNaturalInput input____.weight |> Just ) ]


buildTaskIdInput :
    TaskIdInputRequiredFields
    -> TaskIdInput
buildTaskIdInput required____ =
    { uuid = required____.uuid }


type alias TaskIdInputRequiredFields =
    { uuid : LondoGQL.ScalarCodecs.Uuid }


{-| Type for the TaskIdInput input object.
-}
type alias TaskIdInput =
    { uuid : LondoGQL.ScalarCodecs.Uuid }


{-| Encode a TaskIdInput into a value that can be used as an argument.
-}
encodeTaskIdInput : TaskIdInput -> Value
encodeTaskIdInput input____ =
    Encode.maybeObject
        [ ( "uuid", (LondoGQL.ScalarCodecs.codecs |> LondoGQL.Scalar.unwrapEncoder .codecUuid) input____.uuid |> Just ) ]


buildTaskKeyInput :
    TaskKeyInputRequiredFields
    -> TaskKeyInput
buildTaskKeyInput required____ =
    { projectId = required____.projectId, taskId = required____.taskId }


type alias TaskKeyInputRequiredFields =
    { projectId : ProjectIdInput
    , taskId : TaskIdInput
    }


{-| Type for the TaskKeyInput input object.
-}
type alias TaskKeyInput =
    { projectId : ProjectIdInput
    , taskId : TaskIdInput
    }


{-| Encode a TaskKeyInput into a value that can be used as an argument.
-}
encodeTaskKeyInput : TaskKeyInput -> Value
encodeTaskKeyInput input____ =
    Encode.maybeObject
        [ ( "projectId", encodeProjectIdInput input____.projectId |> Just ), ( "taskId", encodeTaskIdInput input____.taskId |> Just ) ]


buildUserCreation :
    UserCreationRequiredFields
    -> UserCreation
buildUserCreation required____ =
    { nickname = required____.nickname, email = required____.email, password = required____.password, token = required____.token }


type alias UserCreationRequiredFields =
    { nickname : String
    , email : String
    , password : String
    , token : String
    }


{-| Type for the UserCreation input object.
-}
type alias UserCreation =
    { nickname : String
    , email : String
    , password : String
    , token : String
    }


{-| Encode a UserCreation into a value that can be used as an argument.
-}
encodeUserCreation : UserCreation -> Value
encodeUserCreation input____ =
    Encode.maybeObject
        [ ( "nickname", Encode.string input____.nickname |> Just ), ( "email", Encode.string input____.email |> Just ), ( "password", Encode.string input____.password |> Just ), ( "token", Encode.string input____.token |> Just ) ]


buildUserIdInput :
    UserIdInputRequiredFields
    -> UserIdInput
buildUserIdInput required____ =
    { uuid = required____.uuid }


type alias UserIdInputRequiredFields =
    { uuid : LondoGQL.ScalarCodecs.Uuid }


{-| Type for the UserIdInput input object.
-}
type alias UserIdInput =
    { uuid : LondoGQL.ScalarCodecs.Uuid }


{-| Encode a UserIdInput into a value that can be used as an argument.
-}
encodeUserIdInput : UserIdInput -> Value
encodeUserIdInput input____ =
    Encode.maybeObject
        [ ( "uuid", (LondoGQL.ScalarCodecs.codecs |> LondoGQL.Scalar.unwrapEncoder .codecUuid) input____.uuid |> Just ) ]
