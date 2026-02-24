package cellsociety.Model.NeighborStrategy;

import java.util.List;

/** Four neighbors: N, S, E, W only. */
public class CardinalNeighborStrategy implements NeighborStrategy {

    private static final List<int[]> CARDINAL_OFFSETS = List.of(
        new int[] { OFFSET_PREV_ROW_OR_COL, OFFSET_SAME_ROW_OR_COL }, // N
        new int[] { OFFSET_SAME_ROW_OR_COL, OFFSET_PREV_ROW_OR_COL }, // W
        new int[] { OFFSET_SAME_ROW_OR_COL, OFFSET_NEXT_ROW_OR_COL }, // E
        new int[] { OFFSET_NEXT_ROW_OR_COL, OFFSET_SAME_ROW_OR_COL }  // S
    );

    public List<int[]> getNeighborOffsets() {
        return CARDINAL_OFFSETS;
    }
    
}
