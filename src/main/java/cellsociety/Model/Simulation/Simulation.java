package cellsociety.Model.Simulation;

import java.util.Stack;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.ReadOnlyGrid;
import cellsociety.Model.Grid.MutableGrid;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;

/** Runs a single simulation: applies a GameRule to a grid and maintains step-back history. */
public class Simulation {

  /** Minimum number of states on the stack (current only) before stepBack is allowed. */
  private static final int MIN_STACK_SIZE_FOR_STEP_BACK = 1;

  private ArrayGrid myGrid; // current grid (mutable concrete type, but rules see it as ReadOnlyGrid)
  private final GameRule myRule;
  private final Stack<ArrayGrid> myStateStack;

  public Simulation(ArrayGrid grid, GameRule rule) {
    this.myGrid = grid;
    this.myRule = rule;
    this.myStateStack = new Stack<>();
    this.myStateStack.push(copyGrid(grid)); // store snapshot of initial state
  }

  /**
   * Advance one step:
   * - read only from current grid
   * - write all next states into a new grid
   * - push snapshot of the previous current grid for undo
   */
  public void step() {
    ReadOnlyGrid current = myGrid;

    ArrayGrid next = new ArrayGrid(current.getRows(), current.getCols(), current.getNeighborStrategy());
    MutableGrid nextMutable = next;

    for (int r = 0; r < current.getRows(); r++) {
      for (int c = 0; c < current.getCols(); c++) {
        CellState newState = myRule.getNextState(current, r, c);
        nextMutable.setCellState(r, c, newState);
      }
    }

    // push snapshot of the old current state, then move to next
    myStateStack.push(copyGrid(myGrid));
    myGrid = next;
  }

  public int getStateStackSize() {
    return myStateStack.size();
  }

  public void stepBack() {
    if (myStateStack.size() <= MIN_STACK_SIZE_FOR_STEP_BACK) {
      throw new IllegalStateException("Cannot step back from initial state");
    }

    // discard the most recent snapshot (which corresponds to the state right before current step)
    // and restore the new top snapshot
    myStateStack.pop();
    myGrid = copyGrid(myStateStack.peek());
  }

  /**
   * toggles the cell at (row, col) to the next state in this simulation's cycle
   */
  public void toggleCellState(int row, int col) {
    CellState next = myRule.getNextStateInCycle(myGrid.getCellState(row, col));
    myGrid.setCellState(row, col, next);

    // keep undo history consistent: update the latest snapshot too
    // (the top snapshot represents "current" in your old logic; we preserve that behavior)
    ArrayGrid top = myStateStack.peek();
    top.setCellState(row, col, next);
  }

  /**
   * Sets the grid to the given state and updates the undo stack so stepBack() history stays correct.
   */
  public void setState(ReadOnlyGrid state) {
    // create a concrete ArrayGrid copy so we own the data
    this.myGrid = copyGrid(state);
    this.myStateStack.clear();
    this.myStateStack.push(copyGrid(this.myGrid));
  }

  public int getRows() {
    return myGrid.getRows();
  }

  public int getCols() {
    return myGrid.getCols();
  }

  public int getState(int row, int column) {
    return myRule.fromState(myGrid.getCellState(row, column));
  }

  public int getGeneration() {
    return myStateStack.size() - 1;
  }

  public ReadOnlyGrid getReadOnlyGrid() {
    return myGrid;
  }

  private static ArrayGrid copyGrid(ReadOnlyGrid src) {
    NeighborStrategy strategy = src.getNeighborStrategy();
    ArrayGrid copy = new ArrayGrid(src.getRows(), src.getCols(), strategy);
    for (int r = 0; r < src.getRows(); r++) {
      for (int c = 0; c < src.getCols(); c++) {
        copy.setCellState(r, c, src.getCellState(r, c));
      }
    }
    return copy;
  }
}