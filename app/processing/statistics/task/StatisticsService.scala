package processing.statistics.task

import math.Positive
import processing.statistics.TaskWithSimulation
import services.task.Progress
import spire.math.Rational

object StatisticsService {

  def incompleteOfTask(
      taskWithSimulation: TaskWithSimulation,
      numberOfTasks: Positive,
      numberOfCountedTasks: Option[Positive]
  ): IncompleteTaskStatistics = {
    val progress = taskWithSimulation.task.progress
    val mean     = Rational(progress.reached.toBigInt, progress.reachable.natural.toBigInt)

    IncompleteTaskStatistics(
      mean = 100 * mean,
      total = afterForTask(taskWithSimulation, numberOfTasks),
      counted = numberOfCountedTasks.fold(After.zero)(afterForTask(taskWithSimulation, _))
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
    * the difference is k / (n * reachable).
    */
  def differenceAfterValue(
      numberOfElements: Positive,
      reachable: Positive,
      value: BigInt
  ): Rational =
    Rational(
      value,
      numberOfElements.natural.toBigInt * reachable.natural.toBigInt
    )

}
