package cellsociety.view;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.ReadOnlyGrid;
import java.util.Map;
import javafx.scene.Node;

/**
 * View-only contract for rendering a simulation without exposing interactive controls.
 *
 * Purpose:
 * Supports multiple simultaneous simulation displays by providing a small surface area
 * the presenter can drive (grid + generation label).
 *
 * @author Emilio Garcia
 */
public interface SimulationRenderer {

  /**
   * Initializes the renderer for a grid of the given dimensions.
   */
  void setGrid(int rows, int cols);

  /**
   * Renders the grid's current state using the provided state-to-color mapping.
   */
  void render(ReadOnlyGrid grid, Map<CellState, String> stateColors);

  /**
   * Updates the displayed generation counter.
   */
  void setGeneration(int generation);

  /**
   * @return root JavaFX node for embedding in layouts (TilePane, FlowPane, etc.)
   */
  Node getNode();
}
