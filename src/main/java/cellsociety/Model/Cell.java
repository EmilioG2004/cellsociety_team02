package cellsociety.Model;

import cellsociety.Model.CellState.CellState;

/** A single cell that holds a simulation state. */
public abstract class Cell {

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
