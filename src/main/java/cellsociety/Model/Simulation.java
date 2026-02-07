package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;
public class Simulation {
    private Grid myGrid;
    private GameRule myRule;
    private GridHandler myGridHandler;
    public Simulation(Grid grid, GameRule rule) {
        this.myGridHandler = new GridHandler(grid);
        this.myGrid = grid;
        this.myRule = rule;
    }
    public void step() {
        CellState[][] currentStates = myGrid.getCellStates();
        CellState[][] nextStates = myRule.getNextStates(currentStates);
        myGrid.setCellsState(nextStates);
    }
    public GameRule getRule() {
        return myRule;
    }
    public GridHandler getGridHandler() {
        return myGridHandler;
    }
}
