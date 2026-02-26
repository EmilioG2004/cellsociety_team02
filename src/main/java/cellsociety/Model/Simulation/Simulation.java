package cellsociety.Model.Simulation;

import java.util.Stack;
import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.Grid.MutableGrid;
import cellsociety.Model.Grid.ReadOnlyGrid;

/** Runs a single simulation: applies a GameRule to a grid and maintains step-back history. */
public class Simulation {

    /** Minimum number of states on the stack (current only) before stepBack is allowed. */
    private static final int MIN_STACK_SIZE_FOR_STEP_BACK = 1;

    private final MutableGrid myGrid;
    private final GameRule myRule;
    private final Stack<CellState[][]> myStateStack;

    public Simulation(MutableGrid grid, GameRule rule) {
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
    public int getRows()
    {
        return myGrid.getRows();
    }
    public int getCols()
    {
        return myGrid.getCols();
    }
    public int getState(int row, int column)
    {
        return myGrid.getStateInt(row, column);
    }
    public int getGeneration() {
        return myStateStack.size() - 1;
    }

}
