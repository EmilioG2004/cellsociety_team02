/**
 * Cell state API: per-cell state in the grid.
 *
 * <p>{@link cellsociety.Model.CellState.CellState} is the marker interface: {@code toInt()} for
 * serialization and view color lookup, {@code getNextInCycle()} for click-to-cycle in the UI.
 * Rules define concrete state types (enums or classes) and implement this interface.</p>
 */
package cellsociety.Model.CellState;
