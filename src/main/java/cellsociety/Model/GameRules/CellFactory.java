package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;

/**
 * Creates cell states from integer codes (e.g. from config). Used when building a grid
 * so callers need only this capability, not the full {@link GameRule}.
 *
 * @author Michael Lei
 */
public interface CellFactory {

  /** Creates a cell state from a config integer code. */
  CellState fromInt(int value);
}
