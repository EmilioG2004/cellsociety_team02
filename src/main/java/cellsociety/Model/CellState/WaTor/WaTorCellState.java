package cellsociety.Model.CellState.WaTor;

import cellsociety.Model.CellState.CellState;

/**
 * CellState for the Wa-Tor simulation. Wraps a {@link Creature} (null means water/empty).
 * Equality is based on creature type only (not breed counter or energy), so map lookups
 * and legend display work correctly across different simulation steps.
 *
 * @author Michael Lei
 */
public class WaTorCellState implements CellState {

  /** Int encoding for config and fromInt. */
  public static final int WATER_INT = 0;
  public static final int FISH_INT = 1;
  public static final int SHARK_INT = 2;

  /** Representative instance for an empty (water) cell. */
  public static final WaTorCellState WATER = new WaTorCellState(null);
  /** Representative instance for a fish cell. */
  public static final WaTorCellState FISH = new WaTorCellState(new Fish());
  /** Representative instance for a shark cell. */
  public static final WaTorCellState SHARK = new WaTorCellState(new Shark(5));

  private final Creature creature;

  public WaTorCellState(Creature creature) {
    this.creature = creature;
  }

  public static WaTorCellState water() {
    return new WaTorCellState(null);
  }

  public Creature getCreature() {
    return creature;
  }

  public boolean isWater() {
    return creature == null;
  }

  public boolean isFish() {
    return creature instanceof Fish;
  }

  public boolean isShark() {
    return creature instanceof Shark;
  }

  @Override
  public int toInt() {
    if (creature == null) return WATER_INT;
    if (creature instanceof Fish) return FISH_INT;
    return SHARK_INT;
  }

  @Override
  public CellState getNextInCycle() {
    if (creature == null) return new WaTorCellState(new Fish());
    if (creature instanceof Fish) return new WaTorCellState(new Shark(5));
    return water();
  }

  /**
   * Two WaTorCellState instances are equal if they hold the same type of creature
   * (both water, both fish, or both shark), regardless of breed counter or energy.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof WaTorCellState other)) return false;
    if (creature == null) return other.creature == null;
    if (other.creature == null) return false;
    return creature.getClass() == other.creature.getClass();
  }

  @Override
  public int hashCode() {
    return creature == null ? 0 : creature.getClass().hashCode();
  }
}
