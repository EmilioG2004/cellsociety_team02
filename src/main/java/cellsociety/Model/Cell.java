package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;

import java.util.List;

public class Cell implements ReadOnlyCell {

  private CellState currentState;
  private CellState nextState = null;

  public Cell(CellState state) {
    this.currentState = state;
  }

  public CellState getState() {
    return this.currentState;
  }

  public void setState(CellState state) {
    this.currentState = state;
  }

  public void updateState() {
    this.currentState = nextState;
    this.nextState = null;
  }

  public void updateNextState(GameRule rules, List<ReadOnlyCell> neighbors) {
    this.nextState = rules.getNextState(this.currentState, neighbors);
  }
}
