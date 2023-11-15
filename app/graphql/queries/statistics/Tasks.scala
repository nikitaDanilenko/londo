package graphql.queries.statistics

import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class Tasks(
    total: Int,
    counting: Int
)

object Tasks {

  implicit val fromInternal: Transformer[processing.statistics.dashboard.Tasks, Tasks] =
    Transformer
      .define[processing.statistics.dashboard.Tasks, Tasks]
      .buildTransformer

  implicit val outputType: OutputType[Tasks] = deriveObjectType[Unit, Tasks]()

}
