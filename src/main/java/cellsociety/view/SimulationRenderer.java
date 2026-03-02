package cellsociety.view;

import cellsociety.Model.Grid.GridViewData;
import javafx.scene.Node;

/**
 * Minimal view contract for rendering a simulation without interactive controls.
 *
 * <p>Purpose: lets presenters drive multiple simulation displays (e.g., multi-sim dashboard)
 * without exposing the full {@link SimulationView} API.</p>
 *
 * <p>Assumptions: implementations maintain their own internal view state and size.</p>
 *
 * <p>Dependencies: {@link GridViewData} for grid state and JavaFX {@link Node} for embedding.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SimulationRenderer renderer = new SimulationPanel(bundle);
 * renderer.setGrid(rows, cols);
 * renderer.render(sim.getGridViewData());
 * }</pre>
 *
 * @author Emilio Garcia
 */
public interface SimulationRenderer {

  /**
   * Initializes the renderer for a grid of the given dimensions.
   *
   * <p>Purpose: allocates the underlying visual grid once per simulation.</p>
   *
   * <p>Assumptions: {@code rows} and {@code cols} are positive.</p>
   *
   * <p>Exceptions: may throw {@link IllegalArgumentException} if rows/cols are non-positive
   * (implementation-defined).</p>
   *
   * <p>Return value: none.</p>
   *
   * @param rows number of grid rows
   * @param cols number of grid columns
   */
  void setGrid(int rows, int cols);

  /**
   * Renders the grid's current state.
   *
   * <p>Purpose: updates cell visuals to match the simulation state.</p>
   *
   * <p>Assumptions: {@code grid} dimensions match the values passed to {@link #setGrid}.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param grid read-only view data of the simulation grid
   */
  void render(GridViewData grid);

  /**
   * Updates the displayed generation counter.
   *
   * <p>Purpose: keeps the UI in sync with the model's generation count.</p>
   *
   * <p>Assumptions: generation is non-negative.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param generation current generation value
   */
  void setGeneration(int generation);

  /**
   * Returns the root JavaFX node for embedding in layouts.
   *
   * <p>Assumptions: implementers return a stable node instance.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return JavaFX node for this renderer
   */
  Node getNode();
}
