package cellsociety.Model.NeighborStrategy;

import java.util.List;

/**
 * Four neighbors: N, S, E, W only (Von Neumann neighborhood).
 *
 * @author Michael Lei
 */
public class VonNeumannNeighborStrategy implements NeighborStrategy {

    private static final List<int[]> VON_NEUMANN_OFFSETS = List.of(
        new int[] { OFFSET_PREV_ROW_OR_COL, OFFSET_SAME_ROW_OR_COL }, // N
        new int[] { OFFSET_SAME_ROW_OR_COL, OFFSET_PREV_ROW_OR_COL }, // W
        new int[] { OFFSET_SAME_ROW_OR_COL, OFFSET_NEXT_ROW_OR_COL }, // E
        new int[] { OFFSET_NEXT_ROW_OR_COL, OFFSET_SAME_ROW_OR_COL }  // S
    );

    public List<int[]> getNeighborOffsets() {
        return VON_NEUMANN_OFFSETS;
    }

}
