package cellsociety.view.multiview;

import cellsociety.Model.Grid.ViewGrid;
import cellsociety.view.SimulationView;
import javafx.scene.Node;

/**
 * Minimal view contract for rendering a simulation without interactive controls.
 *
 *Purpose: lets presenters drive multiple simulation displays (e.g., multi-sim dashboard)
 * without exposing the full {@link SimulationView} API.
 *
 *Assumptions: implementations maintain their own internal view state and size.
 *
 *Dependencies: {@link ViewGrid} for grid state and JavaFX {@link Node} for embedding.
 *
 *Example usage:
 * Example:
 * SimulationRenderer renderer = new SimulationPanel(bundle);
 * renderer.setGrid(rows, cols);
 * renderer.render(sim.getViewGrid());
 *
 *
 * @author Emilio Garcia
 */
public interface SimulationRenderer {

  /**
   * Initializes the renderer for a grid of the given dimensions.
   *
   *Purpose: allocates the underlying visual grid once per simulation.
   *
   *Assumptions: {@code rows} and {@code cols} are positive.
   *
   *Exceptions: may throw {@link IllegalArgumentException} if rows/cols are non-positive
   * (implementation-defined).
   *
   *Return value: none.
   *
   * @param rows number of grid rows
   * @param cols number of grid columns
   */
  void setGrid(int rows, int cols);

  /**
   * Renders the grid's current state.
   *
   *Purpose: updates cell visuals to match the simulation state.
   *
   *Assumptions: {@code grid} dimensions match the values passed to {@link #setGrid}.
   *
   *Exceptions: none.
   *
   *Return value: none.
   *
   * @param grid read-only view data of the simulation grid
   */
  void render(ViewGrid grid);

  /**
   * Updates the displayed generation counter.
   *
   *Purpose: keeps the UI in sync with the model's generation count.
   *
   *Assumptions: generation is non-negative.
   *
   *Exceptions: none.
   *
   *Return value: none.
   *
   * @param generation current generation value
   */
  void setGeneration(int generation);

  /**
   * Returns the root JavaFX node for embedding in layouts.
   *
   *Assumptions: implementers return a stable node instance.
   *
   *Exceptions: none.
   *
   * @return JavaFX node for this renderer
   */
  Node getNode();
}
