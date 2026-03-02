package cellsociety.Model.Simulation;

import java.util.Stack;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Grid.ViewGrid;
import cellsociety.Model.Grid.ReadOnlyGrid;

/**
 * Runs a single simulation: applies a GameRule to a grid and maintains step-back history.
 *
 * @author Michael Lei
 */
public class Simulation {

  /** Minimum number of states on the stack (current only) before stepBack is allowed. */
  private static final int MIN_STACK_SIZE_FOR_STEP_BACK = 1;

  private Grid myGrid;
  private final GameRule myRule;
  private final Stack<Grid> myStateStack;

  public Simulation(Grid grid, GameRule rule) {
    this.myGrid = grid;
    this.myRule = rule;
    this.myStateStack = new Stack<>();
    this.myStateStack.push(myGrid.deepCopy());
  }

  /**
   * Advance one step: passes current grid to the rule, which returns the next grid.
   * Pushes a snapshot of the current state for undo before advancing.
   */
  public void step() {
    myGrid = myRule.getNextGrid(myGrid);
    myStateStack.push(myGrid.deepCopy());
  }

  public int getStateStackSize() {
    return myStateStack.size();
  }

  public void stepBack() {
    if (myStateStack.size() <= MIN_STACK_SIZE_FOR_STEP_BACK) {
      throw new IllegalStateException("Cannot step back from initial state");
    }
    myStateStack.pop();
    myGrid = myStateStack.peek().deepCopy();
  }

  /**
   * Toggles the cell at (row, col) to the next state in this simulation's cycle.
   */
  public void toggleCellState(int row, int col) {
    CellState next = myGrid.getCellState(row, col).getNextInCycle();
    myGrid.setCellState(row, col, next);
    myStateStack.peek().setCellState(row, col, next);
  }

  /**
   * Resets the grid to the given Grid state and clears undo history.
   */
  public void setState(Grid state) {
    this.myGrid = state.deepCopy();
    this.myStateStack.clear();
    this.myStateStack.push(this.myGrid.deepCopy());
  }

  public int getGeneration() {
    return myStateStack.size() - 1;
  }

  public ViewGrid getGridViewData() {
    return myGrid;
  }

  public ReadOnlyGrid getReadOnlyGrid() {
    return myGrid;
  }

  public GameRule getRule() {
    return myRule;
  }
}
