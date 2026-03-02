package cellsociety.Model.CellState.WaTor;

import java.util.List;
import java.util.Random;

/**
 * A fish in the Wa-Tor simulation. Reproduces after BREED_THRESHOLD steps.
 *
 * @author Michael Lei
 */
public class Fish extends Creature {

  /** Default steps before a fish can reproduce (config key if made configurable). */
  public static final int BREED_THRESHOLD = 4;

  public Fish() {
    super(0);
  }

  public Fish(int breedCounter) {
    super(breedCounter);
  }

  @Override
  public int getBreedThreshold() {
    return BREED_THRESHOLD;
  }

  /** Returns a fish that has spent one turn (breed counter incremented). */
  public Fish afterTurn() {
    return new Fish(getBreedCounter() + 1);
  }

  /** Returns a newborn or post-breed parent fish (breed counter reset to 0). */
  public Fish afterBreeding() {
    return new Fish(0);
  }

  /**
   * Takes one turn given the list of open water neighbor positions [row,col].
   * Returns what to write at current cell and optionally at a chosen destination.
   */
  public WaTorTurnResult act(List<int[]> openWaterNeighbors, Random rand) {
    if (openWaterNeighbors == null || openWaterNeighbors.isEmpty()) {
      return WaTorTurnResult.single(new WaTorCellState(afterTurn()));
    }
    int[] dest = openWaterNeighbors.get(rand.nextInt(openWaterNeighbors.size()));
    int nr = dest[0], nc = dest[1];
    if (canBreed()) {
      return WaTorTurnResult.withDest(
          new WaTorCellState(afterBreeding()),
          nr, nc,
          new WaTorCellState(afterBreeding()));
    }
    return WaTorTurnResult.withDest(
        WaTorCellState.water(),
        nr, nc,
        new WaTorCellState(afterTurn()));
  }
}
