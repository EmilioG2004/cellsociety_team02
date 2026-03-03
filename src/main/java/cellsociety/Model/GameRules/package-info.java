/**
 * Game rules API: transition logic and cell-state creation.
 *
 * <p>{@link cellsociety.Model.GameRules.GameRule} is the main contract: given the current
 * {@link cellsociety.Model.Grid.Grid}, return the next grid; provide a default
 * {@link cellsociety.Model.NeighborStrategy.NeighborStrategy} and
 * {@link cellsociety.Model.GameRules.CellFactory#fromInt} for building initial grids.</p>
 *
 * <p>{@link cellsociety.Model.GameRules.CellFactory} is the minimal interface for creating
 * {@link cellsociety.Model.CellState.CellState} from integer codes (e.g. config). The factory
 * uses this when constructing grids so it does not depend on a full rule instance.</p>
 */
package cellsociety.Model.GameRules;
