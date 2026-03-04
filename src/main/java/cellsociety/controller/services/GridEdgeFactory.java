package cellsociety.controller.services;

import static cellsociety.view.constants.SimulationUiConstants.ERROR_MSG_UNSUPPORTED_GRID_EDGE_PREFIX;

import cellsociety.Model.Grid.ControllerGrid;
import cellsociety.Model.GridEdge.CylindricalEdge;
import cellsociety.Model.GridEdge.FiniteEdge;
import cellsociety.Model.GridEdge.GridEdge;
import cellsociety.Model.GridEdge.MirroredEdge;
import cellsociety.Model.GridEdge.ToroidalEdge;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;
import cellsociety.controller.multiview.SimulationSlot;
import cellsociety.view.constants.ResourcePaths;
import java.util.List;

/**
 * Creates and applies grid edge policies based on UI choice values.
 *
 * @author Emilio Garcia
 */
public class GridEdgeFactory {

  /**
   * Applies the selected edge policy to all loaded simulations in the provided slots.
   */
  public void applyToLoadedSimulations(List<SimulationSlot> slots, String selectedChoice) {
    for (SimulationSlot slot : slots) {
      applyToSlot(slot, selectedChoice);
    }
  }

  /**
   * Applies the selected edge policy to one slot if it has a simulation.
   */
  public void applyToSlot(SimulationSlot slot, String selectedChoice) {
    if (!slot.hasSimulation()) {
      return;
    }
    ControllerGrid grid = slot.getPresenter().getDriver().getSimulation().getControllerGrid();
    NeighborStrategy strategy = grid.getNeighborStrategy();
    strategy.setGridEdge(create(selectedChoice));
    grid.setNeighborStrategy(strategy);
  }

  /**
   * Builds a grid edge policy for the selected choice.
   */
  private GridEdge create(String selectedChoice) {
    if (ResourcePaths.FINITE.equals(selectedChoice)) {
      return new FiniteEdge();
    }
    if (ResourcePaths.TOROIDAL.equals(selectedChoice)) {
      return new ToroidalEdge();
    }
    if (ResourcePaths.CYLINDRICAL.equals(selectedChoice)) {
      return new CylindricalEdge();
    }
    if (ResourcePaths.MIRRORED.equals(selectedChoice)) {
      return new MirroredEdge();
    }
    throw new IllegalArgumentException(ERROR_MSG_UNSUPPORTED_GRID_EDGE_PREFIX + selectedChoice);
  }
}
