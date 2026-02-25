package cellsociety.Model.Creature;

import cellsociety.Model.CellState.WaTorCellState;
import cellsociety.Model.CellState.WaTorTurnResult;
import java.util.List;
import java.util.Random;

/** A shark in the Wa-Tor simulation. Must eat fish to maintain energy; reproduces after BREED_THRESHOLD steps. */
public class Shark extends Creature {

  public static final int BREED_THRESHOLD = 6;

  /** Default energy for a new shark (config key: sharkStartEnergy). */
  public static final int DEFAULT_START_ENERGY = 5;
  /** Default energy gained per fish eaten; should exceed 1 so sharks can survive turns without food (config key: sharkEnergyFromFish). */
  public static final int DEFAULT_ENERGY_FROM_FISH = 4;
  /** Default maximum shark energy cap (config key: sharkMaxEnergy). */
  public static final int DEFAULT_MAX_ENERGY = 15;

  private final int energy;

  public Shark(int energy) {
    super(0);
    this.energy = energy;
  }

  public Shark(int breedCounter, int energy) {
    super(breedCounter);
    this.energy = energy;
  }

  public int getEnergy() {
    return energy;
  }

  public boolean isAlive() {
    return energy > 0;
  }

  @Override
  public int getBreedThreshold() {
    return BREED_THRESHOLD;
  }

  /**
   * Returns a shark after spending one turn without eating, or null if dead.
   * Decrements energy by 1 and increments breed counter.
   */
  public Shark spendTurn() {
    int nextEnergy = energy - 1;
    if (nextEnergy <= 0) return null;
    return new Shark(getBreedCounter() + 1, nextEnergy);
  }

  /**
   * Returns a shark after eating a fish, or null if still dead after eating.
   * Applies energy change (minus 1 for turn, plus energyFromFish) and caps at maxEnergy.
   */
  public Shark afterEating(int energyFromFish, int maxEnergy) {
    int nextEnergy = Math.min(energy - 1 + energyFromFish, maxEnergy);
    if (nextEnergy <= 0) return null;
    return new Shark(getBreedCounter() + 1, nextEnergy);
  }

  /** Returns a shark representing the parent after breeding (breed counter 0, start energy). */
  public Shark breedAsParent(int startEnergy) {
    return new Shark(0, startEnergy);
  }

  /** Returns a shark representing the offspring or the shark that moved onto the prey cell. */
  public Shark breedAsChild(int childEnergy) {
    return new Shark(0, childEnergy);
  }

  /**
   * Tries to eat a fish from the given neighbor positions. Returns null if no fish (caller should call move).
   * Otherwise returns what to write at current cell and at the prey cell.
   */
  public WaTorTurnResult tryEat(List<int[]> fishNeighborPositions, Random rand,
      int startEnergy, int energyFromFish, int maxEnergy) {
    if (fishNeighborPositions == null || fishNeighborPositions.isEmpty()) {
      return null;
    }
    int[] prey = fishNeighborPositions.get(rand.nextInt(fishNeighborPositions.size()));
    int pr = prey[0], pc = prey[1];
    Shark afterEat = afterEating(energyFromFish, maxEnergy);
    if (afterEat == null) {
      return WaTorTurnResult.single(WaTorCellState.water());
    }
    if (canBreed()) {
      return WaTorTurnResult.withDest(
          new WaTorCellState(breedAsParent(startEnergy)),
          pr, pc,
          new WaTorCellState(breedAsChild(afterEat.getEnergy())));
    }
    return WaTorTurnResult.withDest(WaTorCellState.water(), pr, pc, new WaTorCellState(afterEat));
  }

  /**
   * Moves (or stays) given open water neighbor positions. Returns what to write at current and optionally destination.
   */
  public WaTorTurnResult move(List<int[]> openWaterNeighbors, Random rand, int startEnergy) {
    Shark afterTurn = spendTurn();
    if (afterTurn == null) {
      return WaTorTurnResult.single(WaTorCellState.water());
    }
    if (openWaterNeighbors == null || openWaterNeighbors.isEmpty()) {
      return WaTorTurnResult.single(new WaTorCellState(afterTurn));
    }
    int[] dest = openWaterNeighbors.get(rand.nextInt(openWaterNeighbors.size()));
    int nr = dest[0], nc = dest[1];
    if (canBreed()) {
      return WaTorTurnResult.withDest(
          new WaTorCellState(breedAsParent(startEnergy)),
          nr, nc,
          new WaTorCellState(breedAsChild(afterTurn.getEnergy())));
    }
    return WaTorTurnResult.withDest(WaTorCellState.water(), nr, nc, new WaTorCellState(afterTurn));
  }
}
