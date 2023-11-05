package graphql.queries.statistics

import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

import java.math.MathContext

case class After(
    one: BigDecimal,
    completion: BigDecimal,
    simulation: Option[BigDecimal]
)

object After {

  implicit val fromInternal: Transformer[(processing.statistics.task.After, MathContext), After] = {
    case (after, mathContext) =>
      After(
        one = after.one.toBigDecimal(mathContext),
        completion = after.completion.toBigDecimal(mathContext),
        simulation = after.simulation.map(_.toBigDecimal(mathContext))
      )
  }

  implicit val objectType: ObjectType[Unit, After] = deriveObjectType[Unit, After]()

}
