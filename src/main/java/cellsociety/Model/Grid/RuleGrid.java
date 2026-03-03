package cellsociety.Model.Grid;

/**
 * Grid interface for game rules: extends {@link ReadOnlyGrid} with copy operations
 * so rules can create next-generation grids without accessing mutation or strategy-setting methods.
 *
 * @author Michael Lei
 */
public interface RuleGrid extends ReadOnlyGrid {

  /**
   * Returns a new, empty grid of the same concrete type, dimensions, and NeighborStrategy
   * as this grid. All cell states are unset (null). Used by rules to create the
   * next-generation grid without knowing the concrete type.
   */
  Grid emptyCopy();

  /**
   * Returns a new grid of the same concrete type, dimensions, and NeighborStrategy
   * with the same cell states copied. Mutating the returned grid does not affect this grid.
   */
  Grid deepCopy();
}
