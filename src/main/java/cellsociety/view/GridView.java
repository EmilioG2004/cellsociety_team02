package cellsociety.view;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Grid.ReadOnlyGrid;

import java.util.Map;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * Defines the view contract for rendering a simulation grid.
 *
 * <p>Purpose: lets the rest of the UI depend on a stable grid-rendering API while
 * allowing multiple implementations (e.g., GridPane, Canvas) without rewriting
 * controllers or views.</p>
 *
 * <p>Assumptions: the {@link ReadOnlyGrid} dimensions match the implementation's
 * internal grid dimensions; implementers do not mutate the grid.</p>
 *
 * <p>Dependencies: {@link ReadOnlyGrid} for state access, JavaFX {@link Node} and
 * {@link Region} for embedding and sizing, and state-color mappings.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * GridView gridView = new GridPaneView(rows, cols);
 * container.getChildren().add(gridView.getNode());
 * gridView.render(sim.getReadOnlyGrid(), sim.getRule().getStateColors());
 * }</pre>
 *
 * <p>Other details: implementations should keep rendering fast and avoid allocating
 * new nodes on each frame.</p>
 *
 * @author Emilio Garcia
 */
public interface GridView {

  /**
   * Renders the current simulation state.
   *
   * <p>Purpose: updates the visual representation of each cell using the provided
   * grid state and state-to-color mapping.</p>
   *
   * <p>Assumptions: {@code grid} dimensions match the view's dimensions; {@code stateColors}
   * contains an entry for every possible cell state.</p>
   *
   * <p>Exceptions: may throw {@link IllegalArgumentException} if dimensions do not match
   * (implementation-defined).</p>
   *
   * <p>Return value: none.</p>
   *
   * @param grid read-only view of the simulation grid
   * @param stateColors mapping from cell states to color strings (e.g., "#RRGGBB")
   */
  void render(ReadOnlyGrid grid, Map<CellState, String> stateColors);

  /**
   * Clears the visual grid.
   *
   * <p>Purpose: resets the view to a default appearance without mutating simulation data.</p>
   *
   * <p>Assumptions: implementations define what "cleared" means (often a blank grid).</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  void clear();

  /**
   * Registers a click handler for individual cells.
   *
   * <p>Purpose: enables interactive editing or inspection by reporting the clicked row/column.</p>
   *
   * <p>Assumptions: handler may be {@code null} to disable interaction.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param handler callback that receives (row, col) for a clicked cell
   */
  void setOnCellClicked(java.util.function.BiConsumer<Integer, Integer> handler);
  /**
   * Returns the JavaFX node representing this grid view.
   *
   * <p>Purpose: allows the grid view to be inserted into higher-level layouts without
   * exposing implementation details.</p>
   *
   * <p>Assumptions: implementers return a stable node instance.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return node used to display the grid
   */
  Node getNode();

  /**
   * Toggles whether cell outlines are visible.
   *
   * <p>Purpose: provide a quick visual switch between bordered and borderless grids.</p>
   *
   * <p>Assumptions: {@code grid} matches the current dimensions; {@code stateColors} is complete.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param grid read-only grid used to re-apply outlines correctly
   * @param stateColors mapping for state-dependent outline colors (if used)
   */
  void toggleOutline(ReadOnlyGrid grid, Map<CellState, String> stateColors);

  /**
   * Rotates the grid 90 degrees counterclockwise.
   *
   * <p>Purpose: lets users view the simulation from different orientations.</p>
   *
   * <p>Assumptions: the underlying implementation supports rotation.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  void rotateLeft();

  /**
   * Rotates the grid 90 degrees clockwise.
   *
   * <p>Purpose: lets users view the simulation from different orientations.</p>
   *
   * <p>Assumptions: the underlying implementation supports rotation.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  void rotateRight();

  /**
   * Binds cell sizing to a container region.
   *
   * <p>Purpose: ensures the grid scales to available space while preserving cell shape.</p>
   *
   * <p>Assumptions: {@code container} provides valid width/height during layout passes.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param container the region whose size drives cell sizing
   */
  void bindToContainer(Region container);
}
