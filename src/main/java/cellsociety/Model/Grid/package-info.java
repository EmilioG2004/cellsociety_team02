/**
 * Grid API: storage and neighbor lookup for a 2D grid of cell states.
 *
 * <p><b>Interface hierarchy</b></p>
 * <ul>
 *   <li>{@link cellsociety.Model.Grid.ViewGrid} – Minimal view contract: dimensions, {@code getCellStateInt}, neighbor strategy get/set.</li>
 *   <li>{@link cellsociety.Model.Grid.ReadOnlyGrid} – Adds {@code getCellState}, {@code getNeighbors}, {@code isInBounds}, and helpers.</li>
 *   <li>{@link cellsociety.Model.Grid.Grid} – Adds {@code setCellState}, {@code emptyCopy}, {@code deepCopy}.</li>
 * </ul>
 *
 * <p>Rules and simulation use {@link cellsociety.Model.Grid.Grid} / {@link cellsociety.Model.Grid.ReadOnlyGrid};
 * the view should use only {@link cellsociety.Model.Grid.ViewGrid}. Concrete implementation:
 * {@link cellsociety.Model.Grid.ArrayGrid} (created by the factory, not by callers).</p>
 *
 * <p>{@link cellsociety.Model.Grid.Position} is an immutable (row, col) used in neighbor maps.</p>
 */
package cellsociety.Model.Grid;
