package cellsociety.Model;

import java.util.Stack;
import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;

/** Runs a single simulation: applies a GameRule to a Grid and maintains step-back history. */
public class Simulation {

    /** Minimum number of states on the stack (current only) before stepBack is allowed. */
    private static final int MIN_STACK_SIZE_FOR_STEP_BACK = 1;

    private Grid myGrid;
    private GameRule myRule;
    private Stack<CellState[][]> myStateStack;

    public Simulation(Grid grid, GameRule rule) {
        this.myGrid = grid;
        this.myRule = rule;
        this.myStateStack = new Stack<>();
        this.myStateStack.push(myGrid.getCellStates());
    }
    public void step() {
        CellState[][] currentStates = myGrid.getCellStates();
        CellState[][] nextStates = myRule.getNextStates(currentStates);
        myGrid.setCellsState(nextStates);
        myStateStack.push(currentStates);
    }
    public int getStateStackSize() {
        return myStateStack.size();
    }
    public void stepBack() {

        if (myStateStack.size() <= MIN_STACK_SIZE_FOR_STEP_BACK) {
            throw new IllegalStateException("Cannot step back from initial state");

        }
        myStateStack.pop();
        myGrid.setCellsState(myStateStack.peek());
    }
 

    /**
     * toggles the cell at (row, col) to the next state in this simulation's
     */
    public void toggleCellState(int row, int col) {
        CellState next = myRule.getNextStateInCycle(myGrid.getCellState(row, col));
        myGrid.setCellState(row, col, next);
        myStateStack.peek()[row][col] = next;
    }
    /**
     * Sets the grid to the given state and updates the undo stack so stepBack()
     * history stays correct. Use this instead of modifying the grid directly.
     */
    public void setState(CellState[][] state) {
        myGrid.setCellsState(state);
        myStateStack.clear();
        myStateStack.push(state);
    }

    public GameRule getRule() {
        return myRule;
    }
    /**
     * Returns a read-only view of the grid so the view and other consumers
     * can query dimensions and cell state without depending on grid implementation.
     */
    public ReadOnlyGrid getReadOnlyGrid() {
        return myGrid;
    }
}
