package services.access

sealed trait AccessKind

object AccessKind {
  sealed trait Read extends AccessKind
  sealed trait Write extends AccessKind
}
