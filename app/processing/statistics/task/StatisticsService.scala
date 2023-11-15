package processing.statistics.task

import math.Positive
import processing.statistics.TaskWithSimulation
import services.task.Progress
import spire.math.Rational

object StatisticsService {

  // TODO: The options are evaluated every time, but are actually constant over a collection of tasks.
  def incompleteOfTask(
      taskWithSimulation: TaskWithSimulation,
      numberOfTasks: Option[Positive],
      numberOfCountingTasks: Option[Positive]
  ): IncompleteTaskStatistics = {
    val progress = taskWithSimulation.task.progress
    val mean     = Rational(progress.reached.toBigInt, progress.reachable.natural.toBigInt)

    def withZeroDefault(
        number: Option[Positive],
        computationMode: ComputationMode
    ): After = number.fold(After.zero)(afterForTask(computationMode, taskWithSimulation, _))

    IncompleteTaskStatistics(
      mean = 100 * mean,
      total = withZeroDefault(numberOfTasks, ComputationMode.Total),
      counting = withZeroDefault(numberOfCountingTasks, ComputationMode.Counting)
    )
  }

  def afterForTask(
      computationMode: ComputationMode,
      taskWithSimulation: TaskWithSimulation,
      numberOfTasks: Positive
  ): After = {
    val counting = computationMode match {
      case ComputationMode.Total    => true
      case ComputationMode.Counting => taskWithSimulation.task.counting
    }

    After(
      one = differenceAfterOneMore(
        counting = counting,
        numberOfElements = numberOfTasks,
        reachable = taskWithSimulation.task.progress.reachable
      ),
      completion = differenceAfterCompletion(
        counting = counting,
        numberOfElements = numberOfTasks,
        progress = taskWithSimulation.task.progress
      ),
      simulation = taskWithSimulation.simulation.map(
        differenceAfterValue(counting, numberOfTasks, taskWithSimulation.task.progress.reachable, _)
      )
    )
  }

  def differenceAfterOneMore(
      counting: Boolean,
      numberOfElements: Positive,
      reachable: Positive
  ): Rational =
    differenceAfterValue(counting, numberOfElements, reachable, BigInt(1))

  def differenceAfterCompletion(
      counting: Boolean,
      numberOfElements: Positive,
      progress: Progress
  ): Rational =
    differenceAfterValue(
      counting,
      numberOfElements,
      progress.reachable,
      Progress.missing(progress).toBigInt
    )

  /** When comparing the sum of n tasks with the sum of (n - 1), and one task, whose "reached" value is increased by k,
    * the difference is k / (n * reachable). The multiplication with 100 is to get a percentage.
    */
  def differenceAfterValue(
      counting: Boolean,
      numberOfElements: Positive,
      reachable: Positive,
      value: BigInt
  ): Rational =
    if (counting)
      Rational(
        100 * value,
        numberOfElements.natural.toBigInt * reachable.natural.toBigInt
      )
    else Rational.zero

}
