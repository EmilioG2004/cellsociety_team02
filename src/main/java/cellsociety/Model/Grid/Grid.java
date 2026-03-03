package cellsociety.Model.Grid;

import cellsociety.Model.CellState.CellState;

/**
 * Full grid interface: combines {@link RuleGrid} (read-only + copy) and {@link ControllerGrid}
 * (neighbor strategy access) plus cell mutation. Concrete implementations (e.g. ArrayGrid) are
 * created by SimulationFactory and passed to Simulation as this interface, so Simulation stays
 * decoupled from the concrete type.
 *
 * @author Michael Lei
 */
public interface Grid extends RuleGrid, ControllerGrid {

  /**
   * Sets the cell at (row, col) to the given state.
   */
  void setCellState(int row, int col, CellState state);
}
