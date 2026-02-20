package cellsociety.Model.GameRules;
import cellsociety.Model.BasicCell;
import cellsociety.Model.Cell;
import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Utils.MooreNeighborStrategy;
import java.util.Map;
import java.util.List;
import cellsociety.Model.CellState.PercolotionState;

/** Percolation: OPEN cells become FULL when they have enough FULL neighbors (Moore). Optional param: threshold (default 1). */
public class PercolationRule extends GameRule {

    private static final int DEFAULT_THRESHOLD = 1;
    private static final String PARAM_THRESHOLD = "threshold";

    public PercolationRule() {
        super(new MooreNeighborStrategy());
    }

    public PercolationRule(Map<String, Double> parameters) {
        super(new MooreNeighborStrategy(), parameters);
    }

    @Override
    public Cell createCell(CellState state) {
        return new BasicCell(state);
    }

    @Override
    public CellState[][] getNextStates(CellState[][] currentStates) {
        int rows = currentStates.length;
        int cols = currentStates[0].length;
        int threshold;
        if (parameters != null && parameters.containsKey(PARAM_THRESHOLD)) {
            threshold = parameters.get(PARAM_THRESHOLD).intValue();
        } else {
            threshold = DEFAULT_THRESHOLD;
        }
        CellState[][] nextStates = new CellState[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                CellState currentState = currentStates[r][c];
                if(currentState == PercolotionState.CLOSED) {
                    nextStates[r][c] = PercolotionState.CLOSED;
                } else if (currentState == PercolotionState.OPEN) {
                    List<CellState> neighbors = getNeighbors(currentStates, r, c);
                    int fullNeighbors = countNeighborsWithState(neighbors, PercolotionState.FULL);
                    if(fullNeighbors >= threshold) {
                        nextStates[r][c] = PercolotionState.FULL;
                    } else {
                        nextStates[r][c] = PercolotionState.OPEN;
                    }
                } else if (currentState == PercolotionState.FULL) {
                    nextStates[r][c] = PercolotionState.FULL;
                }
            }
        }
        return nextStates;
    }
    @Override
    public Map<CellState, String> getStateColors() {
        return stateColorsFor(PercolotionState.CLOSED, PercolotionState.OPEN, PercolotionState.FULL);
    }
    @Override
    public CellState fromInt(int value) {
        return PercolotionState.fromInt(value);
    }
    @Override
    public CellState getNextStateInCycle(CellState current) {
        return PercolotionState.getNextStateInCycle(current);
    }

    @Override
    public int fromState(CellState state){ return PercolotionState.fromState(state); }
}
