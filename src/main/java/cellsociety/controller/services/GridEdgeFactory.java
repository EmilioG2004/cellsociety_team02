package cellsociety.controller.services;

import cellsociety.Model.Grid.ControllerGrid;
import cellsociety.Model.GridEdge.FiniteEdge;
import cellsociety.Model.GridEdge.GridEdge;
import cellsociety.Model.GridEdge.ToroidalEdge;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;
import cellsociety.controller.multiview.SimulationSlot;
import cellsociety.view.constants.ResourcePaths;
import java.util.List;

/**
 * Creates and applies grid edge policies based on UI choice values.
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
    throw new IllegalArgumentException("Unsupported grid edge choice: " + selectedChoice);
  }
}
