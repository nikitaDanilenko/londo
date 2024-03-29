package processing.statistics.dashboard

import math.Positive
import processing.statistics.TaskWithSimulation
import spire.math.{ Natural, Rational }
import spire.syntax.all._

import scala.util.chaining.scalaUtilChainingOps

object StatisticsService {

  def ofTasks(tasks: Seq[TaskWithSimulation]): DashboardStatistics = {
    val statsInTotal    = statsInCollection(tasks)
    val statsInCounting = statsInCollection(tasks.filter(_.task.counting))

    DashboardStatistics(
      reached = WithSimulation(
        total = statsInTotal.reached,
        counting = statsInCounting.reached,
        simulatedTotal = statsInTotal.reachedSimulated,
        simulatedCounting = statsInCounting.reachedSimulated
      ),
      reachable = WithoutSimulation(
        total = statsInTotal.reachable,
        counting = statsInCounting.reachable
      ),
      absoluteMeans = WithSimulation(
        total = statsInTotal.meanAbsolute,
        counting = statsInCounting.meanAbsolute,
        simulatedTotal = statsInTotal.meanAbsoluteSimulated,
        simulatedCounting = statsInCounting.meanAbsoluteSimulated
      ),
      relativeMeans = WithSimulation(
        total = statsInTotal.meanRelative,
        counting = statsInCounting.meanRelative,
        simulatedTotal = statsInTotal.meanRelativeSimulated,
        simulatedCounting = statsInCounting.meanRelativeSimulated
      ),
      buckets = Buckets(
        total = statsInTotal.bucketMap,
        counting = statsInCounting.bucketMap
      ),
      tasks = Tasks(
        total = statsInTotal.tasks,
        counting = statsInCounting.tasks
      )
    )
  }

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
      reachedSimulated = reached + Natural(allSimulations(tasksWithSimulation)),
      meanAbsolute = meanAbsolute,
      meanAbsoluteSimulated = meanAbsolute + Rational(allSimulations(tasksWithSimulation), numberOfProgresses),
      meanRelative = meanRelative,
      meanRelativeSimulated = meanRelativeSimulated,
      bucketMap = buckets(progresses),
      tasks = tasks.size
    )
  }

  def reachableIn(progresses: Seq[services.task.Progress]): Natural =
    progresses.foldLeft(Natural.zero)((n, p) => n + p.reachable.natural)

  def reachedIn(progresses: Seq[services.task.Progress]): Natural =
    progresses.foldLeft(Natural.zero)((n, p) => n + p.reached)

  def absoluteMeanOf(reached: Natural, reachable: Natural): Rational =
    if (reachable == Natural.zero) Rational.one
    else Rational(BigInt(100) * reached.toBigInt, reachable.toBigInt)

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

  def buckets(progresses: Seq[services.task.Progress]): Map[Bucket, Natural] =
    progresses
      .map(Bucket.forProgress)
      .groupBy(identity)
      .view
      .mapValues(_.pipe(_.size).pipe(Natural(_)))
      .toMap

}
