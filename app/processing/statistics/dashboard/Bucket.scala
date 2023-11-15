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
    Interval.below(Rational(1, 10))                      -> Below10,
    Interval.openUpper(Rational(1, 10), Rational(2, 10)) -> Below20,
    Interval.openUpper(Rational(2, 10), Rational(3, 10)) -> Below30,
    Interval.openUpper(Rational(3, 10), Rational(4, 10)) -> Below40,
    Interval.openUpper(Rational(4, 10), Rational(5, 10)) -> Below50,
    Interval.openUpper(Rational(5, 10), Rational(6, 10)) -> Below60,
    Interval.openUpper(Rational(6, 10), Rational(7, 10)) -> Below70,
    Interval.openUpper(Rational(7, 10), Rational(8, 10)) -> Below80,
    Interval.openUpper(Rational(8, 10), Rational(9, 10)) -> Below90,
    Interval.openUpper(Rational(9, 10), Rational(1))     -> Below100,
    Interval.atOrAbove(Rational(1))                      -> Exactly100
  )

  def forProgress(progress: Progress): Bucket = {
    val rational = progress.value
    intervals.collectFirst { case (interval, bucket) if interval.contains(rational) => bucket }.get
  }

}
