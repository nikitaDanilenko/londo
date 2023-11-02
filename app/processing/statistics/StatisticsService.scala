package processing.statistics

import math.Positive
import spire.math.{ Natural, Rational }
import spire.syntax.all._

object StatisticsService {

  case class TaskWithSimulation(
      task: services.task.Task,
      simulation: Option[BigInt]
  )

  // TODO: Minor nitpick: The number of tasks is computed twice () for the sake of simplicity.
  //       Computing it once is possible, but requires passing it around into every related function.
  def ofTasks(tasks: Seq[TaskWithSimulation]): DashboardStatistics = {
    val statsInTotal   = statsInCollection(tasks)
    val statsInCounted = statsInCollection(tasks.filter(_.task.counting))

    DashboardStatistics(
      total = Progress(
        reached = statsInTotal.reached,
        reachable = statsInTotal.reachable
      ),
      counted = Progress(
        reached = statsInCounted.reached,
        reachable = statsInCounted.reachable
      ),
      absoluteMeans = Means(
        total = statsInTotal.meanAbsolute,
        counted = statsInCounted.meanAbsolute,
        simulatedTotal = statsInTotal.meanAbsoluteSimulated,
        simulatedCounted = statsInCounted.meanAbsoluteSimulated
      ),
      relativeMeans = Means(
        total = statsInTotal.meanRelative,
        counted = statsInCounted.meanRelative,
        simulatedTotal = statsInTotal.meanRelativeSimulated,
        simulatedCounted = statsInCounted.meanRelativeSimulated
      )
    )
  }

  case class StatsInCollection(
      reached: Natural,
      reachable: Natural,
      meanAbsolute: Rational,
      meanAbsoluteSimulated: Rational,
      meanRelative: Rational,
      meanRelativeSimulated: Rational
  )

  def statsInCollection(tasksWithSimulation: Seq[TaskWithSimulation]): StatsInCollection = {
    val tasks                 = tasksWithSimulation.map(_.task)
    val progresses            = tasks.map(_.progress)
    val numberOfProgresses    = Natural(progresses.size)
    val reachable             = reachableIn(progresses)
    val reached               = reachedIn(progresses)
    val meanAbsolute          = absoluteMeanOf(reached, reachable)
    val meanRelative          = relativeMeanOf(numberOfProgresses, progresses)
    val meanRelativeSimulated = simulatedRelativeMean(numberOfProgresses, tasksWithSimulation)

    StatsInCollection(
      reached = reached,
      reachable = reachable,
      meanAbsolute = meanAbsolute,
      meanAbsoluteSimulated = meanAbsolute + Rational(allSimulations(tasksWithSimulation), numberOfProgresses),
      meanRelative = meanRelative,
      meanRelativeSimulated = meanRelativeSimulated
    )
  }

  def reachableIn(progresses: Seq[services.task.Progress]): Natural =
    progresses.foldLeft(Natural.zero)((n, p) => n + p.reachable.natural)

  def reachedIn(progresses: Seq[services.task.Progress]): Natural =
    progresses.foldLeft(Natural.zero)((n, p) => n + p.reached)

  def absoluteMeanOf(reached: Natural, reachable: Natural): Rational =
    if (reachable == Natural.zero) Rational.one
    else Rational(reached.toBigInt, reachable.toBigInt)

  def relativeMeanOf(numberOfValues: Natural, progresses: Seq[services.task.Progress]): Rational =
    Positive(numberOfValues).fold(_ => Rational.one, p => relative(p, progresses))

  def relative(numberOfProgresses: Positive, progresses: Seq[services.task.Progress]): Rational = {
    val scalingFactor = Rational(BigInt(100), numberOfProgresses.natural.toBigInt)
    val preciseRelative = progresses.map { progress =>
      Rational(progress.reached.toBigInt, progress.reachable.natural.toBigInt)
    }.qsum

    scalingFactor * preciseRelative
  }

  def simulatedRelativeMeanOf(progress: services.task.Progress, simulation: BigInt): Rational =
    Rational(progress.reached.toBigInt + simulation, progress.reachable.natural.toBigInt)

  def simulatedRelativeMean(
      numberOfTasks: Natural,
      taskWithSimulation: Seq[TaskWithSimulation]
  ): Rational = {
    val scalingFactor = Rational(BigInt(100), numberOfTasks.toBigInt)
    val preciseRelative = taskWithSimulation.map { taskWithSimulation =>
      simulatedRelativeMeanOf(taskWithSimulation.task.progress, taskWithSimulation.simulation.getOrElse(BigInt(0)))
    }.qsum

    scalingFactor * preciseRelative
  }

  def allSimulations(tasksWithSimulation: Seq[TaskWithSimulation]): BigInt =
    tasksWithSimulation.foldLeft(BigInt(0))((sum, taskWithSimulation) =>
      sum + taskWithSimulation.simulation.getOrElse(BigInt(0))
    )

}
