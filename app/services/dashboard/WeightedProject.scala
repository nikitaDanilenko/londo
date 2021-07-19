package services.dashboard

import services.project.Project
import spire.math.Natural

case class WeightedProject(
    project: Project,
    weight: Natural
)
