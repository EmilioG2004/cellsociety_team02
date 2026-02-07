package cellsociety.Model;

import cellsociety.Model.CellState.CellState;

public class Cell {

  private CellState currentState;


  public Cell(CellState state) {
    this.currentState = state;
  }

  public CellState getState() {
    return this.currentState;
  }

  public void setState(CellState state) {
    this.currentState = state;
  }


}
