package cellsociety.Model.Utils;

import java.util.List;

public class MooreNeighborStrategy implements NeighborStrategy {
    private static final List<int[]> MOORE_OFFSETS = List.of(
            new int[] { -1, -1 }, // NW
            new int[] { -1, 0 }, // N
            new int[] { -1, 1 }, // NE
            new int[] { 0, -1 }, // W
            new int[] { 0, 1 }, // E
            new int[] { 1, -1 }, // SW
            new int[] { 1, 0 }, // S
            new int[] { 1, 1 } // SE
    );

    public List<int[]> getNeighborOffsets() {
        return MOORE_OFFSETS;
    }
}
