package services.task

import cats.data.NonEmptyList
import math.Positive
import spire.syntax.additiveSemigroup._
import spire.syntax.multiplicativeSemigroup._
import utils.fp.NonEmptyListUtil

case class WeightedProgress(
    weight: Positive,
    progress: Progress
)

object WeightedProgress {

  def rescaledProgress(overallWeight: Positive)(weightedProgress: WeightedProgress): Progress =
    Progress.fraction(overallWeight, weightedProgress.weight.natural) * weightedProgress.progress

  def average(progresses: NonEmptyList[WeightedProgress], overallWeight: Positive): Progress =
    NonEmptyListUtil.foldMapLeft(progresses, rescaledProgress(overallWeight))(_ + _)

}
