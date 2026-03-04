package cellsociety.controller.services;

import cellsociety.Model.Grid.ControllerGrid;
import cellsociety.Model.NeighborStrategy.DiagonalNeighborStrategy;
import cellsociety.Model.NeighborStrategy.MooreNeighborStrategy;
import cellsociety.Model.NeighborStrategy.MooreNeighborStrategyRadius2;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;
import cellsociety.Model.NeighborStrategy.VonNeumannNeighborStrategy;
import cellsociety.controller.multiview.SimulationSlot;
import cellsociety.view.constants.ResourcePaths;
import java.util.List;

/**
 * Creates and applies neighbor strategies based on UI choice values.
 */
public class NeighborStrategyFactory {

  /**
   * Applies the selected strategy to all loaded simulations in the provided slots.
   *
   * @param slots simulation slots to update
   * @param selectedChoice canonical choice value from UI
   */
  public void applyToLoadedSimulations(List<SimulationSlot> slots, String selectedChoice) {
    for (SimulationSlot slot : slots) {
      applyToSlot(slot, selectedChoice);
    }
  }

  /**
   * Applies the selected strategy to one slot if it has a simulation.
   *
   * @param slot target slot
   * @param selectedChoice canonical choice value from UI
   */
  public void applyToSlot(SimulationSlot slot, String selectedChoice) {
    if (!slot.hasSimulation()) {
      return;
    }
    ControllerGrid grid = slot.getPresenter().getDriver().getSimulation().getControllerGrid();
    NeighborStrategy updatedStrategy = create(selectedChoice);
    grid.setNeighborStrategy(updatedStrategy);
  }

  /**
   * Builds a neighbor strategy for the selected choice.
   *
   * @param selectedChoice canonical choice value from UI
   * @return neighbor strategy instance
   */
  private NeighborStrategy create(String selectedChoice) {
    if (ResourcePaths.VON_NEUMANN.equals(selectedChoice)) {
      return new VonNeumannNeighborStrategy();
    }
    if (ResourcePaths.DIAGONAL.equals(selectedChoice)) {
      return new DiagonalNeighborStrategy();
    }
    if (ResourcePaths.MOORE_RADIUS_2.equals(selectedChoice)) {
      return new MooreNeighborStrategyRadius2();
    }
    return new MooreNeighborStrategy();
  }
}
