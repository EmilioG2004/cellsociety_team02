package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.WaTorCellState;
import cellsociety.Model.CellState.WaTorTurnResult;
import cellsociety.Model.Creature.Fish;
import cellsociety.Model.Creature.Shark;
import cellsociety.Model.NeighborStrategy.CardinalNeighborStrategy;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;
import cellsociety.Model.NeighborStrategy.WrappingNeighborStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Wa-Tor World: fish and sharks move on cardinal neighbors. Fish reproduce after
 * Fish.BREED_THRESHOLD steps. Sharks eat fish for energy, reproduce after Shark.BREED_THRESHOLD
 * steps, and die when energy reaches zero.
 *
 * <p>Turn logic lives in {@link Fish#act} and {@link Shark#tryEat}/{@link Shark#move}; this rule
 * orchestrates (grid, neighbors, shuffle) and applies their {@link WaTorTurnResult}s.
 */
public class WaTorRule extends GameRule {

  private static final String SHARK_START_ENERGY = "sharkStartEnergy";
  private static final String SHARK_ENERGY_FROM_FISH = "sharkEnergyFromFish";
  private static final String SHARK_MAX_ENERGY = "sharkMaxEnergy";

  private static final int STATE_VALUE_WATER = 0;
  private static final int STATE_VALUE_FISH = 1;
  private static final int STATE_VALUE_SHARK = 2;
  private static final String ERROR_INVALID_STATE = "Invalid WaTor state: ";

  private static final Map<String, Double> DEFAULT_PARAMETERS = Map.of(
      SHARK_START_ENERGY, (double) Shark.DEFAULT_START_ENERGY,
      SHARK_ENERGY_FROM_FISH, (double) Shark.DEFAULT_ENERGY_FROM_FISH,
      SHARK_MAX_ENERGY, (double) Shark.DEFAULT_MAX_ENERGY);

  private final Random rand = new Random();

  public WaTorRule() {
    this(DEFAULT_PARAMETERS);
  }

  public WaTorRule(Map<String, Double> parameters) {
    super(new WrappingNeighborStrategy(new CardinalNeighborStrategy()), parameters);
  }

  public WaTorRule(NeighborStrategy strategy, Map<String, Double> parameters) {
    super(strategy, parameters);
  }

  @Override
  public CellState fromInt(int value) {
    return switch (value) {
      case STATE_VALUE_WATER -> WaTorCellState.water();
      case STATE_VALUE_FISH  -> new WaTorCellState(new Fish());
      case STATE_VALUE_SHARK -> new WaTorCellState(new Shark(getSharkStartEnergy()));
      default -> throw new IllegalArgumentException(ERROR_INVALID_STATE + value);
    };
  }

  @Override
  public int fromState(CellState state) {
    if (state instanceof WaTorCellState wcs) {
      if (wcs.isWater()) return STATE_VALUE_WATER;
      if (wcs.isFish())  return STATE_VALUE_FISH;
      return STATE_VALUE_SHARK;
    }
    throw new IllegalArgumentException(ERROR_INVALID_STATE + state);
  }

  @Override
  public CellState getNextStateInCycle(CellState current) {
    if (current instanceof WaTorCellState wcs) {
      if (wcs.isWater()) return new WaTorCellState(new Fish());
      if (wcs.isFish())  return new WaTorCellState(new Shark(getSharkStartEnergy()));
      return WaTorCellState.water();
    }
    return WaTorCellState.water();
  }

  @Override
  public Map<CellState, String> getStateColors() {
    return stateColorsFor(WaTorCellState.WATER, WaTorCellState.FISH, WaTorCellState.SHARK);
  }

  @Override
  public CellState[][] getNextStates(CellState[][] currentStates) {
    int rows = currentStates.length;
    int cols = rows == 0 ? 0 : currentStates[0].length;

    WaTorCellState[][] current = toWaTorGrid(currentStates, rows, cols);
    WaTorCellState[][] next = copyGrid(current, rows, cols);

    Set<String> claimed = new HashSet<>();
    Set<String> eaten = new HashSet<>();

    List<int[]> fishPositions = collectPositions(current, rows, cols, WaTorCellState::isFish);
    List<int[]> sharkPositions = collectPositions(current, rows, cols, WaTorCellState::isShark);

    Collections.shuffle(fishPositions, rand);
    Collections.shuffle(sharkPositions, rand);

    int startEnergy = getSharkStartEnergy();
    int energyFromFish = getEnergyFromFish();
    int maxEnergy = getSharkMaxEnergy();

    for (int[] pos : sharkPositions) {
      List<int[]> neighbors = getNeighborPositions(rows, cols, pos[0], pos[1]);
      List<int[]> fishSpots = fishNeighbors(neighbors, current, eaten);
      Shark shark = (Shark) current[pos[0]][pos[1]].getCreature();

      WaTorTurnResult result = shark.tryEat(fishSpots, rand, startEnergy, energyFromFish, maxEnergy);
      boolean ate = (result != null);
      if (result == null) {
        List<int[]> openSpots = waterNeighbors(neighbors, current, claimed);
        result = shark.move(openSpots, rand, startEnergy);
      }
      applyResult(pos[0], pos[1], result, next, claimed, eaten, ate);
    }

    for (int[] pos : fishPositions) {
      if (eaten.contains(key(pos[0], pos[1]))) continue;
      List<int[]> neighbors = getNeighborPositions(rows, cols, pos[0], pos[1]);
      List<int[]> openSpots = waterNeighbors(neighbors, current, claimed);
      Fish fish = (Fish) current[pos[0]][pos[1]].getCreature();
      WaTorTurnResult result = fish.act(openSpots, rand);
      applyResult(pos[0], pos[1], result, next, claimed, eaten, false);
    }

    return next;
  }

  private void applyResult(int r, int c, WaTorTurnResult result,
      WaTorCellState[][] next, Set<String> claimed, Set<String> eaten, boolean destWasEaten) {
    next[r][c] = result.atCurrent();
    claimed.add(key(r, c));
    if (result.hasDest()) {
      next[result.destR()][result.destC()] = result.atDest();
      claimed.add(key(result.destR(), result.destC()));
      if (destWasEaten) eaten.add(key(result.destR(), result.destC()));
    }
  }

  private static WaTorCellState[][] toWaTorGrid(CellState[][] states, int rows, int cols) {
    WaTorCellState[][] result = new WaTorCellState[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        result[r][c] = (states[r][c] instanceof WaTorCellState wcs) ? wcs : WaTorCellState.water();
      }
    }
    return result;
  }

  private static WaTorCellState[][] copyGrid(WaTorCellState[][] src, int rows, int cols) {
    WaTorCellState[][] copy = new WaTorCellState[rows][cols];
    for (int r = 0; r < rows; r++) {
      System.arraycopy(src[r], 0, copy[r], 0, cols);
    }
    return copy;
  }

  private static List<int[]> collectPositions(WaTorCellState[][] grid, int rows, int cols,
      Predicate<WaTorCellState> predicate) {
    List<int[]> positions = new ArrayList<>();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (predicate.test(grid[r][c])) positions.add(new int[]{r, c});
      }
    }
    return positions;
  }

  private static List<int[]> waterNeighbors(List<int[]> neighbors,
      WaTorCellState[][] current, Set<String> claimed) {
    List<int[]> result = new ArrayList<>();
    for (int[] pos : neighbors) {
      if (current[pos[0]][pos[1]].isWater() && !claimed.contains(key(pos[0], pos[1]))) {
        result.add(pos);
      }
    }
    return result;
  }

  private static List<int[]> fishNeighbors(List<int[]> neighbors,
      WaTorCellState[][] current, Set<String> eaten) {
    List<int[]> result = new ArrayList<>();
    for (int[] pos : neighbors) {
      if (current[pos[0]][pos[1]].isFish() && !eaten.contains(key(pos[0], pos[1]))) {
        result.add(pos);
      }
    }
    return result;
  }

  private static String key(int r, int c) {
    return r + "," + c;
  }

  private int getSharkStartEnergy() {
    int start = parameters != null && parameters.containsKey(SHARK_START_ENERGY)
        ? parameters.get(SHARK_START_ENERGY).intValue()
        : Shark.DEFAULT_START_ENERGY;
    return Math.min(start, getSharkMaxEnergy());
  }

  private int getEnergyFromFish() {
    return parameters != null && parameters.containsKey(SHARK_ENERGY_FROM_FISH)
        ? parameters.get(SHARK_ENERGY_FROM_FISH).intValue()
        : Shark.DEFAULT_ENERGY_FROM_FISH;
  }

  private int getSharkMaxEnergy() {
    return parameters != null && parameters.containsKey(SHARK_MAX_ENERGY)
        ? parameters.get(SHARK_MAX_ENERGY).intValue()
        : Shark.DEFAULT_MAX_ENERGY;
  }
}

