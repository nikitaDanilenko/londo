package services.task

import cats.data.NonEmptyList
import math.Positive
import spire.syntax.additiveSemigroup._
import spire.syntax.multiplicativeSemigroup._

case class WeightedProgress(
    weight: Positive,
    progress: Progress
)

object WeightedProgress {

  def rescaledProgress(overallWeight: Positive)(weightedProgress: WeightedProgress): Progress =
    Progress.fraction(overallWeight, weightedProgress.weight.natural) * weightedProgress.progress

  def average(progresses: NonEmptyList[WeightedProgress]): Progress = {
    val overallWeight = progresses.tail
      .foldLeft(progresses.head.weight)((w, wp) => w + wp.weight)

    progresses.tail.foldLeft(rescaledProgress(overallWeight)(progresses.head))((r, wp) =>
      r + rescaledProgress(overallWeight)(wp)
    )

  }

}
