package cellsociety.Model;

import java.util.List;

public class Cell implements ReadOnlyCell {
  private int currentState;
  private int nextState = -1;

  public Cell(int state) {
    this.currentState = state;
  }

  public int getState() {
    return this.currentState;
  }
  public void setState(int state) {
    this.currentState = state;
  }

  public void updateState() {
    this.currentState = nextState;
    this.nextState = -1;
  }

  public void updateNextState(GameRule rules, List<ReadOnlyCell> neighbors) {
    this.nextState = rules.getNextState(this.currentState, neighbors);
  }
}
