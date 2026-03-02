package cellsociety.Model.CellState.WaTor;

/**
 * A living creature in the Wa-Tor simulation. Subclasses represent Fish and Shark.
 * Instances are treated as value objects: each simulation step produces new instances
 * with updated counters rather than mutating existing ones.
 *
 * @author Michael Lei
 */
public abstract class Creature {

  private final int breedCounter;

  protected Creature(int breedCounter) {
    this.breedCounter = breedCounter;
  }

  public int getBreedCounter() {
    return breedCounter;
  }

  /** Returns the number of steps this creature must survive before it can reproduce. */
  public abstract int getBreedThreshold();

  public boolean canBreed() {
    return breedCounter >= getBreedThreshold();
  }
}
