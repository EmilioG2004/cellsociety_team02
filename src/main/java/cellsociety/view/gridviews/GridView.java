package cellsociety.view.gridviews;

import cellsociety.Model.Grid.ViewGrid;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * Defines the view contract for rendering a simulation grid.
 *
 *Purpose: lets the rest of the UI depend on a stable grid-rendering API while
 * allowing multiple implementations (e.g., GridPane, Canvas) without rewriting
 * controllers or views.
 *
 *Assumptions: the {@link ViewGrid} dimensions match the implementation's
 * internal grid dimensions; implementers do not mutate the grid.
 *
 *Dependencies: {@link ViewGrid} for state access, JavaFX {@link Node} and
 * {@link Region} for embedding and sizing.
 *
 *Example usage:
 * Example:
 * GridView gridView = new GridPaneView(rows, cols);
 * container.getChildren().add(gridView.getNode());
 * gridView.render(sim.getViewGrid());
 *
 *
 *Other details: implementations should keep rendering fast and avoid allocating
 * new nodes on each frame.
 *
 * @author Emilio Garcia
 */
public interface GridView {

  /**
   * Renders the current simulation state.
   *
   *Purpose: updates the visual representation of each cell using the provided
   * grid state.
   *
   *Assumptions: {@code grid} dimensions match the view's dimensions.
   *
   *Exceptions: may throw {@link IllegalArgumentException} if dimensions do not match
   * (implementation-defined).
   *
   *Return value: none.
   *
   * @param grid read-only view data of the simulation grid
   */
  void render(ViewGrid grid);

  /**
   * Clears the visual grid.
   *
   *Purpose: resets the view to a default appearance without mutating simulation data.
   *
   *Assumptions: implementations define what "cleared" means (often a blank grid).
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  void clear();

  /**
   * Registers a click handler for individual cells.
   *
   *Purpose: enables interactive editing or inspection by reporting the clicked row/column.
   *
   *Assumptions: handler may be {@code null} to disable interaction.
   *
   *Exceptions: none.
   *
   *Return value: none.
   *
   * @param handler callback that receives (row, col) for a clicked cell
   */
  void setOnCellClicked(java.util.function.BiConsumer<Integer, Integer> handler);
  /**
   * Returns the JavaFX node representing this grid view.
   *
   *Purpose: allows the grid view to be inserted into higher-level layouts without
   * exposing implementation details.
   *
   *Assumptions: implementers return a stable node instance.
   *
   *Exceptions: none.
   *
   * @return node used to display the grid
   */
  Node getNode();

  /**
   * Toggles whether cell outlines are visible.
   *
   *Purpose: provide a quick visual switch between bordered and borderless grids.
   *
   *Assumptions: {@code grid} matches the current dimensions; {@code stateColors} is complete.
   *
   *Exceptions: none.
   *
   *Return value: none.
   *
   * @param grid read-only grid used to re-apply outlines correctly
   */
  void toggleOutline(ViewGrid grid);

  /**
   * Rotates the grid 90 degrees counterclockwise.
   *
   *Purpose: lets users view the simulation from different orientations.
   *
   *Assumptions: the underlying implementation supports rotation.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  void rotateLeft();

  /**
   * Rotates the grid 90 degrees clockwise.
   *
   *Purpose: lets users view the simulation from different orientations.
   *
   *Assumptions: the underlying implementation supports rotation.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  void rotateRight();

  /**
   * Binds cell sizing to a container region.
   *
   *Purpose: ensures the grid scales to available space while preserving cell shape.
   *
   *Assumptions: {@code container} provides valid width/height during layout passes.
   *
   *Exceptions: none.
   *
   *Return value: none.
   *
   * @param container the region whose size drives cell sizing
   */
  void bindToContainer(Region container);

  /**
   * Sets the mapping from integer state values to color strings.
   *
   *Purpose: decouple rendering colors from hardcoded values so controller/config
   * can provide simulation-specific palettes.
   *
   *Assumptions: keys are state integers and values are JavaFX-compatible color strings.
   *
   *Exceptions: none.
   *
   *Return value: none.
   *
   * @param stateColors mapping from state value to color string
   */
  void setStateColors(Map<Integer, String> stateColors);
}
