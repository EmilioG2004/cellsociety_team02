package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Grid.RuleGrid;
import cellsociety.Model.NeighborStrategy.MooreNeighborStrategy;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;

import java.util.Map;

/**
 * Conway's Game of Life: Moore neighborhood, birth on 3, survive on 2 or 3.
 *
 * @author EmilioGarcia
 */
public class ConwayGameOfLifeRule extends GameRule {

  private static final int SURVIVE_NEIGHBOR_COUNT_LOW = 2;
  private static final int SURVIVE_NEIGHBOR_COUNT_HIGH = 3;
  private static final int BIRTH_NEIGHBOR_COUNT = 3;

  public ConwayGameOfLifeRule() {
    super(Map.of());
  }

  public ConwayGameOfLifeRule(Map<String, Double> parameters) {
    super(parameters);
  }

  @Override
  public NeighborStrategy getDefaultNeighborStrategy() {
    return new MooreNeighborStrategy();
  }

  @Override
  public Grid getNextGrid(RuleGrid currentGrid) {
    int rows = currentGrid.getRows();
    int cols = currentGrid.getCols();
    Grid next = currentGrid.emptyCopy();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        CellState currentState = currentGrid.getCellState(r, c);
        int aliveNeighbors = currentGrid.countNeighborsWithState(r, c, GameOfLifeState.ALIVE);
        if (currentState == GameOfLifeState.ALIVE && (aliveNeighbors == SURVIVE_NEIGHBOR_COUNT_LOW || aliveNeighbors == SURVIVE_NEIGHBOR_COUNT_HIGH)) {
          next.setCellState(r, c, GameOfLifeState.ALIVE);
        } else if (currentState == GameOfLifeState.DEAD && aliveNeighbors == BIRTH_NEIGHBOR_COUNT) {
          next.setCellState(r, c, GameOfLifeState.ALIVE);
        } else {
          next.setCellState(r, c, GameOfLifeState.DEAD);
        }
      }
    }
    return next;
  }

  @Override
  public CellState fromInt(int value) {
    return GameOfLifeState.fromInt(value);
  }
}
