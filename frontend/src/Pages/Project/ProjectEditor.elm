module Pages.Project.ProjectEditor exposing (..)

import Configuration exposing (Configuration)
import Either exposing (Either)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet(..))
import Language.Language as Language exposing (Language)
import List.Nonempty
import LondoGQL.InputObject exposing (ProjectUpdate)
import LondoGQL.Object
import LondoGQL.Object.Accessors
import LondoGQL.Object.NonEmptyList
import LondoGQL.Object.Project
import LondoGQL.Object.ProjectId
import LondoGQL.Object.UserId
import LondoGQL.Query as Query
import LondoGQL.Scalar exposing (Uuid)
import Pages.Util.RequestUtil as RequestUtil
import Types.Accessors as Accessors exposing (Accessors)
import Types.ProjectId as ProjectId exposing (ProjectId)
import Types.UserId exposing (UserId)
import Util.Editing exposing (Editing)


type alias Model =
    { token : String
    , configuration : Configuration
    , language : Language.ProjectEditor
    , ownProjects : List (Either ProjectInformation (Editing ProjectInformation ProjectUpdate))
    , writeAccessProjects : List (Either ProjectInformation (Editing ProjectInformation ProjectUpdate))
    }


type alias ProjectInformation =
    { id : ProjectId
    , name : String
    , description : Maybe String
    , ownerId : UserId
    , flatIfSingleTask : Bool
    , readAccessors : Accessors
    , writeAccessors : Accessors
    }


type Msg
    = AddProject
    | GotAddProjectResponse (RequestUtil.GraphQLDataOrError Uuid)
    | UpdateProject ProjectId ProjectUpdate
    | SaveProjectEdit ProjectId
    | GotSaveProjectResponse ProjectId (RequestUtil.GraphQLDataOrError ProjectInformation)
    | EnterEditProjectAt ProjectId
    | ExitEditProjectAt ProjectId
    | DeleteProject ProjectId
    | GotDeleteProjectResponse (RequestUtil.GraphQLDataOrError ProjectId)
    | GotFetchOwnProjectsResponse (RequestUtil.GraphQLDataOrError (List ProjectInformation))
    | GotFetchWriteAccessProjectsResponse (RequestUtil.GraphQLDataOrError (List ProjectInformation))


type alias Flags =
    { token : String
    , configuration : Configuration
    , language : Language
    }


init : Flags -> ( Model, Cmd Msg )
init flags =
    let
        model =
            { token = flags.token
            , configuration = flags.configuration
            , language = flags.language.projectEditor
            , ownProjects = []
            , writeAccessProjects = []
            }
    in
    ( model, Cmd.batch [ fetchOwnProjects model, fetchWriteAccessProjects model ] )


fetchOwnProjects : Model -> Cmd Msg
fetchOwnProjects model =
    Query.fetchOwn
        projectInformationSelection
        |> RequestUtil.queryWith (graphQLRequestParametersOf model GotFetchOwnProjectsResponse)


fetchWriteAccessProjects : Model -> Cmd Msg
fetchWriteAccessProjects model =
    Query.fetchWithWriteAccess projectInformationSelection
        |> RequestUtil.queryWith (graphQLRequestParametersOf model GotFetchWriteAccessProjectsResponse)


projectInformationSelection : SelectionSet ProjectInformation LondoGQL.Object.Project
projectInformationSelection =
    SelectionSet.map7 ProjectInformation
        (LondoGQL.Object.Project.id LondoGQL.Object.ProjectId.uuid |> SelectionSet.map ProjectId)
        LondoGQL.Object.Project.name
        LondoGQL.Object.Project.description
        (LondoGQL.Object.Project.ownerId LondoGQL.Object.UserId.uuid |> SelectionSet.map UserId)
        LondoGQL.Object.Project.flatIfSingleTask
        (SelectionSet.map2 (\isAllowList userIds -> Accessors.from { isAllowList = isAllowList, userIds = userIds })
            (LondoGQL.Object.Project.readAccessors LondoGQL.Object.Accessors.isAllowList)
            (LondoGQL.Object.Project.readAccessors userIdsSelection)
        )
        (SelectionSet.map2 (\isAllowList userIds -> Accessors.from { isAllowList = isAllowList, userIds = userIds })
            (LondoGQL.Object.Project.writeAccessors LondoGQL.Object.Accessors.isAllowList)
            (LondoGQL.Object.Project.writeAccessors userIdsSelection)
        )


userIdsSelection : SelectionSet (Maybe (List.Nonempty.Nonempty UserId)) LondoGQL.Object.Accessors
userIdsSelection =
    LondoGQL.Object.Accessors.userIds
        (SelectionSet.map2 List.Nonempty.Nonempty
            (LondoGQL.Object.NonEmptyList.head LondoGQL.Object.UserId.uuid |> SelectionSet.map UserId)
            (LondoGQL.Object.NonEmptyList.tail LondoGQL.Object.UserId.uuid |> SelectionSet.map (List.map UserId))
        )


graphQLRequestParametersOf : Model -> (RequestUtil.GraphQLDataOrError a -> Msg) -> RequestUtil.GraphQLRequestParameters a Msg
graphQLRequestParametersOf model gotResponse =
    { endpoint = model.configuration.graphQLEndpoint
    , token = model.token
    , gotResponse = gotResponse
    }
