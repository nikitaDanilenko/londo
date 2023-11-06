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
      numberOfCountedTasks: Option[Positive]
  ): IncompleteTaskStatistics = {
    val progress = taskWithSimulation.task.progress
    val mean     = Rational(progress.reached.toBigInt, progress.reachable.natural.toBigInt)

    def withZeroDefault(number: Option[Positive]): After = number.fold(After.zero)(afterForTask(taskWithSimulation, _))

    IncompleteTaskStatistics(
      mean = 100 * mean,
      total = withZeroDefault(numberOfTasks),
      counted = withZeroDefault(numberOfCountedTasks)
    )
  }

  def afterForTask(
      taskWithSimulation: TaskWithSimulation,
      numberOfTasks: Positive
  ): After =
    After(
      one = differenceAfterOneMore(numberOfTasks, taskWithSimulation.task.progress.reachable),
      completion = differenceAfterCompletion(numberOfTasks, taskWithSimulation.task.progress),
      simulation = taskWithSimulation.simulation.map(
        differenceAfterValue(numberOfTasks, taskWithSimulation.task.progress.reachable, _)
      )
    )

  def differenceAfterOneMore(
      numberOfElements: Positive,
      reachable: Positive
  ): Rational =
    differenceAfterValue(numberOfElements, reachable, BigInt(1))

  def differenceAfterCompletion(
      numberOfElements: Positive,
      progress: Progress
  ): Rational =
    differenceAfterValue(
      numberOfElements,
      progress.reachable,
      Progress.missing(progress).toBigInt
    )

  /** When comparing the sum of n tasks with the sum of (n - 1), and one task, whose "reached" value is increased by k,
    * the difference is k / (n * reachable). The multiplication with 100 is to get a percentage.
    */
  def differenceAfterValue(
      numberOfElements: Positive,
      reachable: Positive,
      value: BigInt
  ): Rational =
    Rational(
      100 * value,
      numberOfElements.natural.toBigInt * reachable.natural.toBigInt
    )

}
