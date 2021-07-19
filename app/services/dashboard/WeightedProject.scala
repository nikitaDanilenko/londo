package services.dashboard

import services.project.ResolvedProject
import spire.math.Natural

case class WeightedProject(
    resolvedProject: ResolvedProject,
    weight: Natural
)
