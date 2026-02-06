package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;
public class Simulation {
    private Grid myGrid;
    private GameRule myRule;
    //private SimulationConfig myConfig;

    public Simulation(Grid grid, GameRule rule) {
        this.myGrid = grid;
        this.myRule = rule;
        //this.myConfig = config;
    }
    public void step() {
        CellState[][] currentStates = myGrid.getCellStates();
        CellState[][] nextStates = myRule.getNextStates(currentStates);
        myGrid.setCellsState(nextStates);
    }
    public GameRule getRule() {
        return myRule;
    }
    public ReadOnlyGrid getGrid() {
        return myGrid;
    }
}
