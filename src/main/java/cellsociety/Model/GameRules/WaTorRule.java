package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.WaTor.Fish;
import cellsociety.Model.CellState.WaTor.Shark;
import cellsociety.Model.CellState.WaTor.WaTorCellState;
import cellsociety.Model.CellState.WaTor.WaTorTurnResult;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Grid.Position;
import cellsociety.Model.Grid.ReadOnlyGrid;
import cellsociety.Model.NeighborStrategy.VonNeumannNeighborStrategy;
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
 *
 * @author Michael Lei
 */
public class WaTorRule extends GameRule {

  private static final String SHARK_START_ENERGY = "sharkStartEnergy";
  private static final String SHARK_ENERGY_FROM_FISH = "sharkEnergyFromFish";
  private static final String SHARK_MAX_ENERGY = "sharkMaxEnergy";

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
    super(parameters != null ? parameters : DEFAULT_PARAMETERS);
  }

  @Override
  public NeighborStrategy getDefaultNeighborStrategy() {
    return new WrappingNeighborStrategy(new VonNeumannNeighborStrategy());
  }

  @Override
  public Grid getNextGrid(Grid currentGrid) {
    Grid next = currentGrid.deepCopy();
    computeNext(currentGrid, next, currentGrid.getRows(), currentGrid.getCols());
    return next;
  }

  private void computeNext(ReadOnlyGrid current, Grid next, int rows, int cols) {
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
      List<int[]> fishSpots = neighborPositionsFiltered(current, pos[0], pos[1], eaten, WaTorCellState::isFish);
      WaTorCellState cell = cast(current.getCellState(pos[0], pos[1]));
      Shark shark = (Shark) cell.getCreature();

      WaTorTurnResult result = shark.tryEat(fishSpots, rand, startEnergy, energyFromFish, maxEnergy);
      boolean ate = (result != null);
      if (result == null) {
        List<int[]> openSpots = neighborPositionsFiltered(current, pos[0], pos[1], claimed, WaTorCellState::isWater);
        result = shark.move(openSpots, rand, startEnergy);
      }
      applyResult(pos[0], pos[1], result, next, claimed, eaten, ate);
    }

    for (int[] pos : fishPositions) {
      if (eaten.contains(key(pos[0], pos[1]))) continue;
      List<int[]> openSpots = neighborPositionsFiltered(current, pos[0], pos[1], claimed, WaTorCellState::isWater);
      WaTorCellState cell = cast(current.getCellState(pos[0], pos[1]));
      Fish fish = (Fish) cell.getCreature();
      WaTorTurnResult result = fish.act(openSpots, rand);
      applyResult(pos[0], pos[1], result, next, claimed, eaten, false);
    }
  }

  private void applyResult(int r, int c, WaTorTurnResult result,
      Grid next, Set<String> claimed, Set<String> eaten, boolean destWasEaten) {
    next.setCellState(r, c, result.atCurrent());
    claimed.add(key(r, c));
    if (result.hasDest()) {
      next.setCellState(result.destR(), result.destC(), result.atDest());
      claimed.add(key(result.destR(), result.destC()));
      if (destWasEaten) eaten.add(key(result.destR(), result.destC()));
    }
  }

  private static List<int[]> collectPositions(ReadOnlyGrid grid, int rows, int cols,
      Predicate<WaTorCellState> predicate) {
    List<int[]> positions = new ArrayList<>();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (predicate.test(cast(grid.getCellState(r, c)))) positions.add(new int[]{r, c});
      }
    }
    return positions;
  }

  /** Neighbor positions (as int[]) where state matches predicate and position is not in excluded set. */
  private static List<int[]> neighborPositionsFiltered(ReadOnlyGrid current, int r, int c,
      Set<String> excluded, Predicate<WaTorCellState> stateMatches) {
    List<int[]> result = new ArrayList<>();
    for (Map.Entry<Position, CellState> e : current.getNeighborStates(r, c).entrySet()) {
      Position pos = e.getKey();
      if (stateMatches.test(cast(e.getValue())) && !excluded.contains(key(pos.row(), pos.col()))) {
        result.add(new int[]{pos.row(), pos.col()});
      }
    }
    return result;
  }

  private static WaTorCellState cast(CellState state) {
    return (state instanceof WaTorCellState wcs) ? wcs : WaTorCellState.water();
  }

  private static String key(int r, int c) {
    return r + "," + c;
  }

  private int getIntParam(String key, int defaultValue) {
    return parameters.containsKey(key) ? parameters.get(key).intValue() : defaultValue;
  }

  private int getSharkStartEnergy() {
    return Math.min(getIntParam(SHARK_START_ENERGY, Shark.DEFAULT_START_ENERGY), getSharkMaxEnergy());
  }

  private int getEnergyFromFish() {
    return getIntParam(SHARK_ENERGY_FROM_FISH, Shark.DEFAULT_ENERGY_FROM_FISH);
  }

  private int getSharkMaxEnergy() {
    return getIntParam(SHARK_MAX_ENERGY, Shark.DEFAULT_MAX_ENERGY);
  }

  @Override
  public CellState fromInt(int value) {
    return switch (value) {
      case WaTorCellState.WATER_INT -> WaTorCellState.water();
      case WaTorCellState.FISH_INT  -> new WaTorCellState(new Fish());
      case WaTorCellState.SHARK_INT -> new WaTorCellState(new Shark(getSharkStartEnergy()));
      default -> throw new IllegalArgumentException(ERROR_INVALID_STATE + value);
    };
  }
}
