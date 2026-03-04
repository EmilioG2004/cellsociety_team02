package cellsociety.Model.Grid;

import cellsociety.Model.NeighborStrategy.NeighborStrategy;

/**
 * Grid interface for the controller: extends the rendering-only {@link ViewGrid} with
 * neighbor-strategy mutation so the controller can change the neighborhood type
 * (e.g. Moore vs Von Neumann) at runtime. The read-only getter lives on {@link ReadOnlyGrid}.
 *
 * @author Michael Lei
 */
public interface ControllerGrid extends ViewGrid {

  /** Returns the neighbor strategy attached to the grid (e.g. Moore, Von Neumann). */
  NeighborStrategy getNeighborStrategy();

  /** Sets the neighbor strategy so the controller can change neighborhood dynamically. */
  void setNeighborStrategy(NeighborStrategy strategy);
}
