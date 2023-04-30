-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module LondoGQL.InputObject exposing (..)

import Graphql.Internal.Builder.Argument as Argument exposing (Argument)
import Graphql.Internal.Builder.Object as Object
import Graphql.Internal.Encode as Encode exposing (Value)
import Graphql.OptionalArgument exposing (OptionalArgument(..))
import Graphql.SelectionSet exposing (SelectionSet)
import Json.Decode as Decode
import LondoGQL.Enum.LogoutMode
import LondoGQL.Enum.TaskKind
import LondoGQL.Enum.Visibility
import LondoGQL.Interface
import LondoGQL.Object
import LondoGQL.Scalar
import LondoGQL.ScalarCodecs
import LondoGQL.Union


buildConfirmDeletionInput :
    ConfirmDeletionInputRequiredFields
    -> ConfirmDeletionInput
buildConfirmDeletionInput required____ =
    { deletionToken = required____.deletionToken }


type alias ConfirmDeletionInputRequiredFields =
    { deletionToken : String }


{-| Type for the ConfirmDeletionInput input object.
-}
type alias ConfirmDeletionInput =
    { deletionToken : String }


{-| Encode a ConfirmDeletionInput into a value that can be used as an argument.
-}
encodeConfirmDeletionInput : ConfirmDeletionInput -> Value
encodeConfirmDeletionInput input____ =
    Encode.maybeObject
        [ ( "deletionToken", Encode.string input____.deletionToken |> Just ) ]


buildConfirmRecoveryInput :
    ConfirmRecoveryInputRequiredFields
    -> ConfirmRecoveryInput
buildConfirmRecoveryInput required____ =
    { recoveryToken = required____.recoveryToken, password = required____.password }


type alias ConfirmRecoveryInputRequiredFields =
    { recoveryToken : String
    , password : String
    }


{-| Type for the ConfirmRecoveryInput input object.
-}
type alias ConfirmRecoveryInput =
    { recoveryToken : String
    , password : String
    }


{-| Encode a ConfirmRecoveryInput into a value that can be used as an argument.
-}
encodeConfirmRecoveryInput : ConfirmRecoveryInput -> Value
encodeConfirmRecoveryInput input____ =
    Encode.maybeObject
        [ ( "recoveryToken", Encode.string input____.recoveryToken |> Just ), ( "password", Encode.string input____.password |> Just ) ]


buildConfirmRegistrationInput :
    ConfirmRegistrationInputRequiredFields
    -> ConfirmRegistrationInput
buildConfirmRegistrationInput required____ =
    { creationToken = required____.creationToken, creationComplement = required____.creationComplement }


type alias ConfirmRegistrationInputRequiredFields =
    { creationToken : String
    , creationComplement : CreationComplement
    }


{-| Type for the ConfirmRegistrationInput input object.
-}
type alias ConfirmRegistrationInput =
    { creationToken : String
    , creationComplement : CreationComplement
    }


{-| Encode a ConfirmRegistrationInput into a value that can be used as an argument.
-}
encodeConfirmRegistrationInput : ConfirmRegistrationInput -> Value
encodeConfirmRegistrationInput input____ =
    Encode.maybeObject
        [ ( "creationToken", Encode.string input____.creationToken |> Just ), ( "creationComplement", encodeCreationComplement input____.creationComplement |> Just ) ]


buildCreateDashboardEntryInput :
    CreateDashboardEntryInputRequiredFields
    -> CreateDashboardEntryInput
buildCreateDashboardEntryInput required____ =
    { dashboardId = required____.dashboardId, dashboardEntryCreation = required____.dashboardEntryCreation }


type alias CreateDashboardEntryInputRequiredFields =
    { dashboardId : DashboardIdInput
    , dashboardEntryCreation : DashboardEntryCreation
    }


{-| Type for the CreateDashboardEntryInput input object.
-}
type alias CreateDashboardEntryInput =
    { dashboardId : DashboardIdInput
    , dashboardEntryCreation : DashboardEntryCreation
    }


{-| Encode a CreateDashboardEntryInput into a value that can be used as an argument.
-}
encodeCreateDashboardEntryInput : CreateDashboardEntryInput -> Value
encodeCreateDashboardEntryInput input____ =
    Encode.maybeObject
        [ ( "dashboardId", encodeDashboardIdInput input____.dashboardId |> Just ), ( "dashboardEntryCreation", encodeDashboardEntryCreation input____.dashboardEntryCreation |> Just ) ]


buildCreateDashboardInput :
    CreateDashboardInputRequiredFields
    -> CreateDashboardInput
buildCreateDashboardInput required____ =
    { dashboardCreation = required____.dashboardCreation }


type alias CreateDashboardInputRequiredFields =
    { dashboardCreation : DashboardCreation }


{-| Type for the CreateDashboardInput input object.
-}
type alias CreateDashboardInput =
    { dashboardCreation : DashboardCreation }


{-| Encode a CreateDashboardInput into a value that can be used as an argument.
-}
encodeCreateDashboardInput : CreateDashboardInput -> Value
encodeCreateDashboardInput input____ =
    Encode.maybeObject
        [ ( "dashboardCreation", encodeDashboardCreation input____.dashboardCreation |> Just ) ]


buildCreateProjectInput :
    CreateProjectInputRequiredFields
    -> (CreateProjectInputOptionalFields -> CreateProjectInputOptionalFields)
    -> CreateProjectInput
buildCreateProjectInput required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { description = Absent }
    in
    { name = required____.name, description = optionals____.description }


type alias CreateProjectInputRequiredFields =
    { name : String }


type alias CreateProjectInputOptionalFields =
    { description : OptionalArgument String }


{-| Type for the CreateProjectInput input object.
-}
type alias CreateProjectInput =
    { name : String
    , description : OptionalArgument String
    }


{-| Encode a CreateProjectInput into a value that can be used as an argument.
-}
encodeCreateProjectInput : CreateProjectInput -> Value
encodeCreateProjectInput input____ =
    Encode.maybeObject
        [ ( "name", Encode.string input____.name |> Just ), ( "description", Encode.string |> Encode.optional input____.description ) ]


buildCreateTaskInput :
    CreateTaskInputRequiredFields
    -> CreateTaskInput
buildCreateTaskInput required____ =
    { projectId = required____.projectId, taskCreation = required____.taskCreation }


type alias CreateTaskInputRequiredFields =
    { projectId : ProjectIdInput
    , taskCreation : TaskCreation
    }


{-| Type for the CreateTaskInput input object.
-}
type alias CreateTaskInput =
    { projectId : ProjectIdInput
    , taskCreation : TaskCreation
    }


{-| Encode a CreateTaskInput into a value that can be used as an argument.
-}
encodeCreateTaskInput : CreateTaskInput -> Value
encodeCreateTaskInput input____ =
    Encode.maybeObject
        [ ( "projectId", encodeProjectIdInput input____.projectId |> Just ), ( "taskCreation", encodeTaskCreation input____.taskCreation |> Just ) ]


buildCreationComplement :
    CreationComplementRequiredFields
    -> (CreationComplementOptionalFields -> CreationComplementOptionalFields)
    -> CreationComplement
buildCreationComplement required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { displayName = Absent }
    in
    { displayName = optionals____.displayName, password = required____.password }


type alias CreationComplementRequiredFields =
    { password : String }


type alias CreationComplementOptionalFields =
    { displayName : OptionalArgument String }


{-| Type for the CreationComplement input object.
-}
type alias CreationComplement =
    { displayName : OptionalArgument String
    , password : String
    }


{-| Encode a CreationComplement into a value that can be used as an argument.
-}
encodeCreationComplement : CreationComplement -> Value
encodeCreationComplement input____ =
    Encode.maybeObject
        [ ( "displayName", Encode.string |> Encode.optional input____.displayName ), ( "password", Encode.string input____.password |> Just ) ]


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
    { header = required____.header, description = optionals____.description, visibility = required____.visibility }


type alias DashboardCreationRequiredFields =
    { header : String
    , visibility : LondoGQL.Enum.Visibility.Visibility
    }


type alias DashboardCreationOptionalFields =
    { description : OptionalArgument String }


{-| Type for the DashboardCreation input object.
-}
type alias DashboardCreation =
    { header : String
    , description : OptionalArgument String
    , visibility : LondoGQL.Enum.Visibility.Visibility
    }


{-| Encode a DashboardCreation into a value that can be used as an argument.
-}
encodeDashboardCreation : DashboardCreation -> Value
encodeDashboardCreation input____ =
    Encode.maybeObject
        [ ( "header", Encode.string input____.header |> Just ), ( "description", Encode.string |> Encode.optional input____.description ), ( "visibility", Encode.enum LondoGQL.Enum.Visibility.toString input____.visibility |> Just ) ]


buildDashboardEntryCreation :
    DashboardEntryCreationRequiredFields
    -> DashboardEntryCreation
buildDashboardEntryCreation required____ =
    { projectId = required____.projectId }


type alias DashboardEntryCreationRequiredFields =
    { projectId : ProjectIdInput }


{-| Type for the DashboardEntryCreation input object.
-}
type alias DashboardEntryCreation =
    { projectId : ProjectIdInput }


{-| Encode a DashboardEntryCreation into a value that can be used as an argument.
-}
encodeDashboardEntryCreation : DashboardEntryCreation -> Value
encodeDashboardEntryCreation input____ =
    Encode.maybeObject
        [ ( "projectId", encodeProjectIdInput input____.projectId |> Just ) ]


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
    { header = required____.header, description = optionals____.description, visibility = required____.visibility }


type alias DashboardUpdateRequiredFields =
    { header : String
    , visibility : LondoGQL.Enum.Visibility.Visibility
    }


type alias DashboardUpdateOptionalFields =
    { description : OptionalArgument String }


{-| Type for the DashboardUpdate input object.
-}
type alias DashboardUpdate =
    { header : String
    , description : OptionalArgument String
    , visibility : LondoGQL.Enum.Visibility.Visibility
    }


{-| Encode a DashboardUpdate into a value that can be used as an argument.
-}
encodeDashboardUpdate : DashboardUpdate -> Value
encodeDashboardUpdate input____ =
    Encode.maybeObject
        [ ( "header", Encode.string input____.header |> Just ), ( "description", Encode.string |> Encode.optional input____.description ), ( "visibility", Encode.enum LondoGQL.Enum.Visibility.toString input____.visibility |> Just ) ]


buildDeleteDashboardEntryInput :
    DeleteDashboardEntryInputRequiredFields
    -> DeleteDashboardEntryInput
buildDeleteDashboardEntryInput required____ =
    { dashboardId = required____.dashboardId, projectId = required____.projectId }


type alias DeleteDashboardEntryInputRequiredFields =
    { dashboardId : DashboardIdInput
    , projectId : ProjectIdInput
    }


{-| Type for the DeleteDashboardEntryInput input object.
-}
type alias DeleteDashboardEntryInput =
    { dashboardId : DashboardIdInput
    , projectId : ProjectIdInput
    }


{-| Encode a DeleteDashboardEntryInput into a value that can be used as an argument.
-}
encodeDeleteDashboardEntryInput : DeleteDashboardEntryInput -> Value
encodeDeleteDashboardEntryInput input____ =
    Encode.maybeObject
        [ ( "dashboardId", encodeDashboardIdInput input____.dashboardId |> Just ), ( "projectId", encodeProjectIdInput input____.projectId |> Just ) ]


buildDeleteDashboardInput :
    DeleteDashboardInputRequiredFields
    -> DeleteDashboardInput
buildDeleteDashboardInput required____ =
    { dashboardId = required____.dashboardId }


type alias DeleteDashboardInputRequiredFields =
    { dashboardId : DashboardIdInput }


{-| Type for the DeleteDashboardInput input object.
-}
type alias DeleteDashboardInput =
    { dashboardId : DashboardIdInput }


{-| Encode a DeleteDashboardInput into a value that can be used as an argument.
-}
encodeDeleteDashboardInput : DeleteDashboardInput -> Value
encodeDeleteDashboardInput input____ =
    Encode.maybeObject
        [ ( "dashboardId", encodeDashboardIdInput input____.dashboardId |> Just ) ]


buildDeleteProjectInput :
    DeleteProjectInputRequiredFields
    -> DeleteProjectInput
buildDeleteProjectInput required____ =
    { projectId = required____.projectId }


type alias DeleteProjectInputRequiredFields =
    { projectId : ProjectIdInput }


{-| Type for the DeleteProjectInput input object.
-}
type alias DeleteProjectInput =
    { projectId : ProjectIdInput }


{-| Encode a DeleteProjectInput into a value that can be used as an argument.
-}
encodeDeleteProjectInput : DeleteProjectInput -> Value
encodeDeleteProjectInput input____ =
    Encode.maybeObject
        [ ( "projectId", encodeProjectIdInput input____.projectId |> Just ) ]


buildDeleteTaskInput :
    DeleteTaskInputRequiredFields
    -> DeleteTaskInput
buildDeleteTaskInput required____ =
    { projectId = required____.projectId, taskId = required____.taskId }


type alias DeleteTaskInputRequiredFields =
    { projectId : ProjectIdInput
    , taskId : TaskIdInput
    }


{-| Type for the DeleteTaskInput input object.
-}
type alias DeleteTaskInput =
    { projectId : ProjectIdInput
    , taskId : TaskIdInput
    }


{-| Encode a DeleteTaskInput into a value that can be used as an argument.
-}
encodeDeleteTaskInput : DeleteTaskInput -> Value
encodeDeleteTaskInput input____ =
    Encode.maybeObject
        [ ( "projectId", encodeProjectIdInput input____.projectId |> Just ), ( "taskId", encodeTaskIdInput input____.taskId |> Just ) ]


buildFetchDashboardInput :
    FetchDashboardInputRequiredFields
    -> FetchDashboardInput
buildFetchDashboardInput required____ =
    { dashboardId = required____.dashboardId }


type alias FetchDashboardInputRequiredFields =
    { dashboardId : DashboardIdInput }


{-| Type for the FetchDashboardInput input object.
-}
type alias FetchDashboardInput =
    { dashboardId : DashboardIdInput }


{-| Encode a FetchDashboardInput into a value that can be used as an argument.
-}
encodeFetchDashboardInput : FetchDashboardInput -> Value
encodeFetchDashboardInput input____ =
    Encode.maybeObject
        [ ( "dashboardId", encodeDashboardIdInput input____.dashboardId |> Just ) ]


buildFetchProjectInput :
    FetchProjectInputRequiredFields
    -> FetchProjectInput
buildFetchProjectInput required____ =
    { projectId = required____.projectId }


type alias FetchProjectInputRequiredFields =
    { projectId : ProjectIdInput }


{-| Type for the FetchProjectInput input object.
-}
type alias FetchProjectInput =
    { projectId : ProjectIdInput }


{-| Encode a FetchProjectInput into a value that can be used as an argument.
-}
encodeFetchProjectInput : FetchProjectInput -> Value
encodeFetchProjectInput input____ =
    Encode.maybeObject
        [ ( "projectId", encodeProjectIdInput input____.projectId |> Just ) ]


buildFetchResolvedDashboardInput :
    FetchResolvedDashboardInputRequiredFields
    -> FetchResolvedDashboardInput
buildFetchResolvedDashboardInput required____ =
    { dashboardId = required____.dashboardId }


type alias FetchResolvedDashboardInputRequiredFields =
    { dashboardId : DashboardIdInput }


{-| Type for the FetchResolvedDashboardInput input object.
-}
type alias FetchResolvedDashboardInput =
    { dashboardId : DashboardIdInput }


{-| Encode a FetchResolvedDashboardInput into a value that can be used as an argument.
-}
encodeFetchResolvedDashboardInput : FetchResolvedDashboardInput -> Value
encodeFetchResolvedDashboardInput input____ =
    Encode.maybeObject
        [ ( "dashboardId", encodeDashboardIdInput input____.dashboardId |> Just ) ]


buildFetchResolvedProjectInput :
    FetchResolvedProjectInputRequiredFields
    -> FetchResolvedProjectInput
buildFetchResolvedProjectInput required____ =
    { projectId = required____.projectId }


type alias FetchResolvedProjectInputRequiredFields =
    { projectId : ProjectIdInput }


{-| Type for the FetchResolvedProjectInput input object.
-}
type alias FetchResolvedProjectInput =
    { projectId : ProjectIdInput }


{-| Encode a FetchResolvedProjectInput into a value that can be used as an argument.
-}
encodeFetchResolvedProjectInput : FetchResolvedProjectInput -> Value
encodeFetchResolvedProjectInput input____ =
    Encode.maybeObject
        [ ( "projectId", encodeProjectIdInput input____.projectId |> Just ) ]


buildFindUserInput :
    FindUserInputRequiredFields
    -> FindUserInput
buildFindUserInput required____ =
    { searchString = required____.searchString }


type alias FindUserInputRequiredFields =
    { searchString : String }


{-| Type for the FindUserInput input object.
-}
type alias FindUserInput =
    { searchString : String }


{-| Encode a FindUserInput into a value that can be used as an argument.
-}
encodeFindUserInput : FindUserInput -> Value
encodeFindUserInput input____ =
    Encode.maybeObject
        [ ( "searchString", Encode.string input____.searchString |> Just ) ]


buildLoginInput :
    LoginInputRequiredFields
    -> LoginInput
buildLoginInput required____ =
    { nickname = required____.nickname, password = required____.password, isValidityUnrestricted = required____.isValidityUnrestricted }


type alias LoginInputRequiredFields =
    { nickname : String
    , password : String
    , isValidityUnrestricted : Bool
    }


{-| Type for the LoginInput input object.
-}
type alias LoginInput =
    { nickname : String
    , password : String
    , isValidityUnrestricted : Bool
    }


{-| Encode a LoginInput into a value that can be used as an argument.
-}
encodeLoginInput : LoginInput -> Value
encodeLoginInput input____ =
    Encode.maybeObject
        [ ( "nickname", Encode.string input____.nickname |> Just ), ( "password", Encode.string input____.password |> Just ), ( "isValidityUnrestricted", Encode.bool input____.isValidityUnrestricted |> Just ) ]


buildLogoutInput :
    LogoutInputRequiredFields
    -> LogoutInput
buildLogoutInput required____ =
    { logoutMode = required____.logoutMode }


type alias LogoutInputRequiredFields =
    { logoutMode : LondoGQL.Enum.LogoutMode.LogoutMode }


{-| Type for the LogoutInput input object.
-}
type alias LogoutInput =
    { logoutMode : LondoGQL.Enum.LogoutMode.LogoutMode }


{-| Encode a LogoutInput into a value that can be used as an argument.
-}
encodeLogoutInput : LogoutInput -> Value
encodeLogoutInput input____ =
    Encode.maybeObject
        [ ( "logoutMode", Encode.enum LondoGQL.Enum.LogoutMode.toString input____.logoutMode |> Just ) ]


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


buildRequestRecoveryInput :
    RequestRecoveryInputRequiredFields
    -> RequestRecoveryInput
buildRequestRecoveryInput required____ =
    { userId = required____.userId }


type alias RequestRecoveryInputRequiredFields =
    { userId : UserIdInput }


{-| Type for the RequestRecoveryInput input object.
-}
type alias RequestRecoveryInput =
    { userId : UserIdInput }


{-| Encode a RequestRecoveryInput into a value that can be used as an argument.
-}
encodeRequestRecoveryInput : RequestRecoveryInput -> Value
encodeRequestRecoveryInput input____ =
    Encode.maybeObject
        [ ( "userId", encodeUserIdInput input____.userId |> Just ) ]


buildRequestRegistrationInput :
    RequestRegistrationInputRequiredFields
    -> RequestRegistrationInput
buildRequestRegistrationInput required____ =
    { userIdentifier = required____.userIdentifier }


type alias RequestRegistrationInputRequiredFields =
    { userIdentifier : UserIdentifier }


{-| Type for the RequestRegistrationInput input object.
-}
type alias RequestRegistrationInput =
    { userIdentifier : UserIdentifier }


{-| Encode a RequestRegistrationInput into a value that can be used as an argument.
-}
encodeRequestRegistrationInput : RequestRegistrationInput -> Value
encodeRequestRegistrationInput input____ =
    Encode.maybeObject
        [ ( "userIdentifier", encodeUserIdentifier input____.userIdentifier |> Just ) ]


buildTaskCreation :
    TaskCreationRequiredFields
    -> (TaskCreationOptionalFields -> TaskCreationOptionalFields)
    -> TaskCreation
buildTaskCreation required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { unit = Absent }
    in
    { name = required____.name, taskKind = required____.taskKind, unit = optionals____.unit, progress = required____.progress, counting = required____.counting }


type alias TaskCreationRequiredFields =
    { name : String
    , taskKind : LondoGQL.Enum.TaskKind.TaskKind
    , progress : ProgressInput
    , counting : Bool
    }


type alias TaskCreationOptionalFields =
    { unit : OptionalArgument String }


{-| Type for the TaskCreation input object.
-}
type alias TaskCreation =
    { name : String
    , taskKind : LondoGQL.Enum.TaskKind.TaskKind
    , unit : OptionalArgument String
    , progress : ProgressInput
    , counting : Bool
    }


{-| Encode a TaskCreation into a value that can be used as an argument.
-}
encodeTaskCreation : TaskCreation -> Value
encodeTaskCreation input____ =
    Encode.maybeObject
        [ ( "name", Encode.string input____.name |> Just ), ( "taskKind", Encode.enum LondoGQL.Enum.TaskKind.toString input____.taskKind |> Just ), ( "unit", Encode.string |> Encode.optional input____.unit ), ( "progress", encodeProgressInput input____.progress |> Just ), ( "counting", Encode.bool input____.counting |> Just ) ]


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


buildTaskUpdate :
    TaskUpdateRequiredFields
    -> (TaskUpdateOptionalFields -> TaskUpdateOptionalFields)
    -> TaskUpdate
buildTaskUpdate required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { unit = Absent }
    in
    { name = required____.name, taskKind = required____.taskKind, unit = optionals____.unit, counting = required____.counting, progressUpdate = required____.progressUpdate }


type alias TaskUpdateRequiredFields =
    { name : String
    , taskKind : LondoGQL.Enum.TaskKind.TaskKind
    , counting : Bool
    , progressUpdate : ProgressUpdate
    }


type alias TaskUpdateOptionalFields =
    { unit : OptionalArgument String }


{-| Type for the TaskUpdate input object.
-}
type alias TaskUpdate =
    { name : String
    , taskKind : LondoGQL.Enum.TaskKind.TaskKind
    , unit : OptionalArgument String
    , counting : Bool
    , progressUpdate : ProgressUpdate
    }


{-| Encode a TaskUpdate into a value that can be used as an argument.
-}
encodeTaskUpdate : TaskUpdate -> Value
encodeTaskUpdate input____ =
    Encode.maybeObject
        [ ( "name", Encode.string input____.name |> Just ), ( "taskKind", Encode.enum LondoGQL.Enum.TaskKind.toString input____.taskKind |> Just ), ( "unit", Encode.string |> Encode.optional input____.unit ), ( "counting", Encode.bool input____.counting |> Just ), ( "progressUpdate", encodeProgressUpdate input____.progressUpdate |> Just ) ]


buildUpdateDashboardInput :
    UpdateDashboardInputRequiredFields
    -> UpdateDashboardInput
buildUpdateDashboardInput required____ =
    { dashboardId = required____.dashboardId, dashboardUpdate = required____.dashboardUpdate }


type alias UpdateDashboardInputRequiredFields =
    { dashboardId : DashboardIdInput
    , dashboardUpdate : DashboardUpdate
    }


{-| Type for the UpdateDashboardInput input object.
-}
type alias UpdateDashboardInput =
    { dashboardId : DashboardIdInput
    , dashboardUpdate : DashboardUpdate
    }


{-| Encode a UpdateDashboardInput into a value that can be used as an argument.
-}
encodeUpdateDashboardInput : UpdateDashboardInput -> Value
encodeUpdateDashboardInput input____ =
    Encode.maybeObject
        [ ( "dashboardId", encodeDashboardIdInput input____.dashboardId |> Just ), ( "dashboardUpdate", encodeDashboardUpdate input____.dashboardUpdate |> Just ) ]


buildUpdatePasswordInput :
    UpdatePasswordInputRequiredFields
    -> UpdatePasswordInput
buildUpdatePasswordInput required____ =
    { password = required____.password }


type alias UpdatePasswordInputRequiredFields =
    { password : String }


{-| Type for the UpdatePasswordInput input object.
-}
type alias UpdatePasswordInput =
    { password : String }


{-| Encode a UpdatePasswordInput into a value that can be used as an argument.
-}
encodeUpdatePasswordInput : UpdatePasswordInput -> Value
encodeUpdatePasswordInput input____ =
    Encode.maybeObject
        [ ( "password", Encode.string input____.password |> Just ) ]


buildUpdateProjectInput :
    UpdateProjectInputRequiredFields
    -> (UpdateProjectInputOptionalFields -> UpdateProjectInputOptionalFields)
    -> UpdateProjectInput
buildUpdateProjectInput required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { description = Absent }
    in
    { projectId = required____.projectId, name = required____.name, description = optionals____.description }


type alias UpdateProjectInputRequiredFields =
    { projectId : ProjectIdInput
    , name : String
    }


type alias UpdateProjectInputOptionalFields =
    { description : OptionalArgument String }


{-| Type for the UpdateProjectInput input object.
-}
type alias UpdateProjectInput =
    { projectId : ProjectIdInput
    , name : String
    , description : OptionalArgument String
    }


{-| Encode a UpdateProjectInput into a value that can be used as an argument.
-}
encodeUpdateProjectInput : UpdateProjectInput -> Value
encodeUpdateProjectInput input____ =
    Encode.maybeObject
        [ ( "projectId", encodeProjectIdInput input____.projectId |> Just ), ( "name", Encode.string input____.name |> Just ), ( "description", Encode.string |> Encode.optional input____.description ) ]


buildUpdateTaskInput :
    UpdateTaskInputRequiredFields
    -> UpdateTaskInput
buildUpdateTaskInput required____ =
    { projectId = required____.projectId, taskId = required____.taskId, taskUpdate = required____.taskUpdate }


type alias UpdateTaskInputRequiredFields =
    { projectId : ProjectIdInput
    , taskId : TaskIdInput
    , taskUpdate : TaskUpdate
    }


{-| Type for the UpdateTaskInput input object.
-}
type alias UpdateTaskInput =
    { projectId : ProjectIdInput
    , taskId : TaskIdInput
    , taskUpdate : TaskUpdate
    }


{-| Encode a UpdateTaskInput into a value that can be used as an argument.
-}
encodeUpdateTaskInput : UpdateTaskInput -> Value
encodeUpdateTaskInput input____ =
    Encode.maybeObject
        [ ( "projectId", encodeProjectIdInput input____.projectId |> Just ), ( "taskId", encodeTaskIdInput input____.taskId |> Just ), ( "taskUpdate", encodeTaskUpdate input____.taskUpdate |> Just ) ]


buildUpdateUserInput :
    UpdateUserInputRequiredFields
    -> (UpdateUserInputOptionalFields -> UpdateUserInputOptionalFields)
    -> UpdateUserInput
buildUpdateUserInput required____ fillOptionals____ =
    let
        optionals____ =
            fillOptionals____
                { displayName = Absent }
    in
    { displayName = optionals____.displayName, email = required____.email }


type alias UpdateUserInputRequiredFields =
    { email : String }


type alias UpdateUserInputOptionalFields =
    { displayName : OptionalArgument String }


{-| Type for the UpdateUserInput input object.
-}
type alias UpdateUserInput =
    { displayName : OptionalArgument String
    , email : String
    }


{-| Encode a UpdateUserInput into a value that can be used as an argument.
-}
encodeUpdateUserInput : UpdateUserInput -> Value
encodeUpdateUserInput input____ =
    Encode.maybeObject
        [ ( "displayName", Encode.string |> Encode.optional input____.displayName ), ( "email", Encode.string input____.email |> Just ) ]


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


buildUserIdentifier :
    UserIdentifierRequiredFields
    -> UserIdentifier
buildUserIdentifier required____ =
    { nickname = required____.nickname, email = required____.email }


type alias UserIdentifierRequiredFields =
    { nickname : String
    , email : String
    }


{-| Type for the UserIdentifier input object.
-}
type alias UserIdentifier =
    { nickname : String
    , email : String
    }


{-| Encode a UserIdentifier into a value that can be used as an argument.
-}
encodeUserIdentifier : UserIdentifier -> Value
encodeUserIdentifier input____ =
    Encode.maybeObject
        [ ( "nickname", Encode.string input____.nickname |> Just ), ( "email", Encode.string input____.email |> Just ) ]
