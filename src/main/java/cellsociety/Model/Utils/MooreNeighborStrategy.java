package cellsociety.Model.Utils;

import java.util.List;

/** Eight neighbors: N, S, E, W, NE, NW, SE, SW. */
public class MooreNeighborStrategy implements NeighborStrategy {

    private static final List<int[]> MOORE_OFFSETS = List.of(
            new int[] { OFFSET_PREV_ROW_OR_COL, OFFSET_PREV_ROW_OR_COL }, // NW
            new int[] { OFFSET_PREV_ROW_OR_COL, OFFSET_SAME_ROW_OR_COL }, // N
            new int[] { OFFSET_PREV_ROW_OR_COL, OFFSET_NEXT_ROW_OR_COL }, // NE
            new int[] { OFFSET_SAME_ROW_OR_COL, OFFSET_PREV_ROW_OR_COL }, // W
            new int[] { OFFSET_SAME_ROW_OR_COL, OFFSET_NEXT_ROW_OR_COL }, // E
            new int[] { OFFSET_NEXT_ROW_OR_COL, OFFSET_PREV_ROW_OR_COL }, // SW
            new int[] { OFFSET_NEXT_ROW_OR_COL, OFFSET_SAME_ROW_OR_COL }, // S
            new int[] { OFFSET_NEXT_ROW_OR_COL, OFFSET_NEXT_ROW_OR_COL }  // SE
    );

    public List<int[]> getNeighborOffsets() {
        return MOORE_OFFSETS;
    }
}
