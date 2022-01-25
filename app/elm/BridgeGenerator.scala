package elm

import elm.Bridge.mkAndWrite
import graphql.types.project.ProjectId
import graphql.types.task.{ Progress, Task, TaskId, TaskKind }
import graphql.types.user.UserId

object BridgeGenerator {

  def main(args: Array[String]): Unit = {
    mkAndWrite[ProjectId]
    mkAndWrite[TaskId]
    mkAndWrite[Task.Plain]
    mkAndWrite[Task.ProjectReference]
    mkAndWrite[UserId]
    mkAndWrite[Progress]
    mkAndWrite[TaskKind]
  }

}
