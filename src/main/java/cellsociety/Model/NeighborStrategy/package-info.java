/**
 * Neighbor strategy API: which grid positions count as neighbors for a cell.
 *
 * <p>{@link cellsociety.Model.NeighborStrategy.NeighborStrategy} defines the contract:
 * {@code getNeighborOffsets()} returns relative (dr, dc) offsets; default methods convert these to
 * valid positions using a {@link cellsociety.Model.GridEdge.GridEdge} policy (e.g.
 * {@link cellsociety.Model.GridEdge.FiniteEdge} for clipping,
 * {@link cellsociety.Model.GridEdge.ToroidalEdge} for wrapping).</p>
 *
 * <p>Standard implementations: Moore (8), Von Neumann (4), diagonal (4 corners), Moore radius 2 (24),
 * etc. The grid holds one strategy; the view can change it via {@link cellsociety.Model.Grid.ViewGrid#setNeighborStrategy}.</p>
 */
package cellsociety.Model.NeighborStrategy;
