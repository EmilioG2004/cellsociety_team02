package cellsociety.Model;
import java.util.Stack;
import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;
public class Simulation {
    private Grid myGrid;
    private GameRule myRule;
    private GridHandler myGridHandler;
    private Stack<CellState[][]> myStateStack;
    public Simulation(Grid grid, GameRule rule) {
        this.myGridHandler = new GridHandler(grid);
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

        if (myStateStack.size() <= 1) {
            throw new IllegalStateException("Cannot step back from initial state");

        }
        myStateStack.pop();
        myGrid.setCellsState(myStateStack.peek());
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
    public GridHandler getGridHandler() {
        return myGridHandler;
    }
}
