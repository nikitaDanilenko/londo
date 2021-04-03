package services.project

sealed trait TaskKind

object TaskKind {
  case object Discrete extends TaskKind
  case object Percentual extends TaskKind
  case object Fraction extends TaskKind
}
