package processing.statistics

import spire.math.Natural

/** Similar to the service level progress, but allows zero reachable values. This is relevant in the case where there is
  * no (counting) task at all.
  */
case class Progress(
    reached: Natural,
    reachable: Natural
)
