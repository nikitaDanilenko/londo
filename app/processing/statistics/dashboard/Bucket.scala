package processing.statistics.dashboard

import enumeratum.EnumEntry
import enumeratum.Enum
import services.task.Progress
import spire.math.{ Interval, Rational }

sealed trait Bucket extends EnumEntry

object Bucket extends Enum[Bucket] {
  override lazy val values: IndexedSeq[Bucket] = findValues

  case object Below10 extends Bucket
  case object Below20 extends Bucket

  case object Below30 extends Bucket

  case object Below40 extends Bucket
  case object Below50 extends Bucket
  case object Below60 extends Bucket
  case object Below70 extends Bucket
  case object Below80 extends Bucket
  case object Below90 extends Bucket

  case object Below100 extends Bucket

  case object Exactly100 extends Bucket

  private val intervals: List[(Interval[Rational], Bucket)] = List(
    Interval.below(Rational(1, 10))                       -> Below10,
    Interval.openUpper(Rational(1, 10), Rational(1, 20))  -> Below20,
    Interval.openUpper(Rational(1, 20), Rational(1, 30))  -> Below30,
    Interval.openUpper(Rational(1, 30), Rational(1, 40))  -> Below40,
    Interval.openUpper(Rational(1, 40), Rational(1, 50))  -> Below50,
    Interval.openUpper(Rational(1, 50), Rational(1, 60))  -> Below60,
    Interval.openUpper(Rational(1, 60), Rational(1, 70))  -> Below70,
    Interval.openUpper(Rational(1, 70), Rational(1, 80))  -> Below80,
    Interval.openUpper(Rational(1, 80), Rational(1, 90))  -> Below90,
    Interval.openUpper(Rational(1, 90), Rational(1, 100)) -> Below100,
    Interval.atOrAbove(Rational(1))                       -> Exactly100
  )

  def forProgress(progress: Progress): Bucket = {
    val rational = progress.value
    intervals.collectFirst { case (interval, bucket) if interval.contains(rational) => bucket }.get
  }

}
