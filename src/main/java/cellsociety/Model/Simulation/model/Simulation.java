package cellsociety.Model.Simulation.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.Grid.ControllerGrid;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Grid.ReadOnlyGrid;
import cellsociety.Model.Grid.ViewGrid;

/**
 * Runs a single simulation: applies a GameRule to a grid and maintains step-back history.
 * Implements {@link SimulationModel} so callers depend on the API, not this concrete class.
 *
 * @author Michael Lei
 */
public class Simulation implements SimulationModel {

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

  @Override
  public void step() {
    myGrid = myRule.getNextGrid(myGrid);
    myStateStack.push(myGrid.deepCopy());
  }

  public int getStateStackSize() {
    return myStateStack.size();
  }

  @Override
  public void stepBack() {
    if (myStateStack.size() <= MIN_STACK_SIZE_FOR_STEP_BACK) {
      throw new IllegalStateException("Cannot step back from initial state");
    }
    myStateStack.pop();
    myGrid = myStateStack.peek().deepCopy();
  }

  @Override
  public void toggleCellState(int row, int col) {
    CellState next = myGrid.getCellState(row, col).getNextInCycle();
    myGrid.setCellState(row, col, next);
    myStateStack.peek().setCellState(row, col, next);
  }

  @Override
  public void setState(Grid state) {
    this.myGrid = state.deepCopy();
    this.myStateStack.clear();
    this.myStateStack.push(this.myGrid.deepCopy());
  }

  @Override
  public int getGeneration() {
    return myStateStack.size() - 1;
  }

  @Override
  public ViewGrid getViewGrid() {
    return myGrid;
  }

  @Override
  public ReadOnlyGrid getReadOnlyGrid() {
    return myGrid;
  }

  @Override
  public ControllerGrid getControllerGrid() {
    return myGrid;
  }

  @Override
  public GameRule getRule() {
    return myRule;
  }

  @Override
  public Map<Integer, Integer> getStateCounts() {
    Map<Integer, Integer> counts = new HashMap<>();
    for (int row = 0; row < myGrid.getRows(); row++) {
      for (int col = 0; col < myGrid.getCols(); col++) {
        int state = myGrid.getCellState(row, col).toInt();
        counts.put(state, counts.getOrDefault(state, 0) + 1);
      }
    }
    return counts;
  }
}
