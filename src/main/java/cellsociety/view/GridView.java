package cellsociety.view;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.ReadOnlyGrid;
import java.util.Map;
import javafx.scene.Node;

/**
 * Defines a common interface for visual components that render a simulation grid.
 *
 * Purpose:
 * This interface allows the view layer to support multiple grid-rendering
 * implementations (such as GridPane-based or Canvas-based views) without
 * coupling the rest of the application to a specific JavaFX layout or drawing
 * technique.
 *
 * Assumptions:
 * - Implementations are responsible for managing their own internal visual state.
 * - The provided ReadOnlyGrid accurately represents the current simulation state
 *   and should not be modified by implementations.
 *
 * Dependencies:
 * - ReadOnlyGrid for accessing grid state (rows, cols, cell state per index).
 * - JavaFX Node so implementations can be embedded in the UI layout.
 *
 * Example usage:
 * GridView gridView = new GridPaneView(rows, cols, cellSize);
 * rootPane.setCenter(gridView.getNode());
 *
 * @author Emilio Garcia

 */
public interface GridView {

  /**
   * Renders the current state of the simulation grid.
   *
   * Purpose:
   * Updates the visual representation of each cell based on the data provided
   * by the ReadOnlyGrid and the associated state-to-color mapping.
   *
   * Assumptions:
   * - The dimensions of the ReadOnlyGrid are compatible with the implementation’s
   *   internal grid representation.
   * - The stateColors map contains entries for all possible cell states.
   *
   * @param grid read-only view of the simulation grid (rows, cols, getCellState)
   * @param stateColors mapping from cell state values to color strings
   */
  void render(ReadOnlyGrid grid, Map<CellState, String> stateColors);

  /**
   * Clears the visual grid.
   *
   * Purpose:
   * Resets the grid to a default visual state without modifying the underlying
   * simulation data.
   *
   * Assumptions:
   * - Implementations define what constitutes a "cleared" state.
   */
  void clear();
  void setOnCellClicked(java.util.function.BiConsumer<Integer, Integer> handler);
  /**
   * Returns the JavaFX node representing this grid view.
   *
   * Purpose:
   * Allows the grid view to be inserted into higher-level layouts without exposing
   * implementation details.
   *
   * @return Node used to display the grid
   */
  Node getNode();

  void toggleOutline(ReadOnlyGrid grid, Map<CellState, String> stateColors);

  void rotateLeft();

  void rotateRight();
}
