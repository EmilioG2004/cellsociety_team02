package cellsociety.Model.Utils;
import java.util.List;
public class CardinalNeighborStrategy implements NeighborStrategy {
    private static final List<int[]> CARDINAL_OFFSETS = List.of(
        new int[] { -1, 0 }, // N
        new int[] { 0, -1 }, // W
        new int[] { 0, 1 }, // E
        new int[] { 1, 0 } // S
    );

    public List<int[]> getNeighborOffsets() {
        return CARDINAL_OFFSETS;
    }
    
}
