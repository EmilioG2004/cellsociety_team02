package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;
import cellsociety.Model.ReadOnlyCell;
import cellsociety.Model.Utils.MooreNeighborStrategy;
import java.util.List;
import java.util.Map;

public class ConwayGameOfLifeRule extends GameRule {

  public ConwayGameOfLifeRule() {
    super(new MooreNeighborStrategy());
  }

  public ConwayGameOfLifeRule(Map<String, String> parameters) {
    super(new MooreNeighborStrategy(), parameters);
  }

  @Override
  public CellState getNextState(CellState currentState, List<ReadOnlyCell> neighbors) {
    int aliveNeighbors = countNeighborsWithState(neighbors, GameOfLifeState.ALIVE);

    if (currentState == GameOfLifeState.ALIVE && (aliveNeighbors == 2 || aliveNeighbors == 3)) {
      return GameOfLifeState.ALIVE;
    } else if (currentState == GameOfLifeState.DEAD && aliveNeighbors == 3) {
      return GameOfLifeState.ALIVE;
    }
    return GameOfLifeState.DEAD;
  }

  @Override
  public Map<CellState, String> getStateColors() {
    return Map.of(GameOfLifeState.DEAD, "WHITE", GameOfLifeState.ALIVE, "GREEN");
  }

  @Override
  public CellState fromInt(int value) {
    return GameOfLifeState.fromInt(value);
  }
}
