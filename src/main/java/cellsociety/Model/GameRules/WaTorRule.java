package cellsociety.Model.GameRules;
import cellsociety.Model.Cell.BasicCell;
import cellsociety.Model.Cell.Cell;
import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.WaTorState;
import cellsociety.Model.NeighborStrategy.CardinalNeighborStrategy;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** Wa-Tor World: fish and sharks move on cardinal neighbors; fish reproduce after N chronons, sharks eat fish for energy and reproduce after M chronons. */
public class WaTorRule extends GameRule {
  private static final String SHARK_START_ENERGY = "sharkStartEnergy";
  private static final String SHARK_ENERGY_FROM_FISH = "sharkEnergyFromFish";
  private static final int DEFAULT_FISH_BIRTH_GENS = 2;
  private static final int DEFAULT_SHARK_BIRTH_GENS = 6;
  /** Sharks lose 1 energy per chronon; this gives at most 5 steps without food so they die off when no fish. */
  private static final int DEFAULT_SHARK_START_ENERGY = 5;
  /** Energy gained per fish eaten; 1 offsets the 1 lost per turn so sharks must keep eating to survive. */
  private static final int DEFAULT_ENERGY_FROM_EATING_FISH = 1;

  /** Default parameters for logical predator-prey behavior: sharks die when fish are scarce. */
  private static final Map<String, Double> DEFAULT_PARAMETERS = Map.of(
      SHARK_START_ENERGY, (double) DEFAULT_SHARK_START_ENERGY,
      SHARK_ENERGY_FROM_FISH, (double) DEFAULT_ENERGY_FROM_EATING_FISH);
  private int[][] fishGenCount;
  private int[][] sharkGenCount;
  private int[][] sharkEnergy;
  private Random rand;

  private static String key(int r, int c) {
    return r + "," + c;
  }

  /** Wa-Tor rule with default parameters (sharks die off when no fish). */
  public WaTorRule() {
    this(DEFAULT_PARAMETERS);
  }

  /** Wa-Tor rule with the given parameters (e.g. sharkStartEnergy, sharkEnergyFromFish). */
  public WaTorRule(Map<String, Double> parameters) {
    super(new CardinalNeighborStrategy(), parameters);
    rand = new Random();
  }

  public WaTorRule(NeighborStrategy strategy, Map<String, Double> parameters) {
    super(strategy, parameters);
    rand = new Random();
  }

  /** Initializes breed counts and shark energy for the grid; no-op if already initialized. */
  public void initEnduringValues(CellState[][] currentStates)
  {
    int rows = currentStates.length;
    int cols = currentStates[0].length;
    if(fishGenCount == null)
    {
      int sharkStartEnergy;
      fishGenCount = new int[rows][cols];
      sharkGenCount = new int[rows][cols];
      sharkEnergy = new int[rows][cols];
      if (parameters != null && parameters.containsKey(SHARK_START_ENERGY))
      {
        double dsharkStartEnergy = parameters.get(SHARK_START_ENERGY);
        sharkStartEnergy = (int) dsharkStartEnergy;
      }
      else {
        sharkStartEnergy = DEFAULT_SHARK_START_ENERGY;
      }
      for (int r = 0; r < currentStates.length; r++) {
        for (int c = 0; c < currentStates[0].length; c++) {
          fishGenCount[r][c] = 0;
          sharkGenCount[r][c] = 0;
          if(currentStates[r][c] == WaTorState.SHARK)
          {
            sharkEnergy[r][c] = sharkStartEnergy;
          }
        }
      }
    }
  }
  @Override
  public Cell createCell(CellState state) {
    return new BasicCell(state);
  }

  /** One generation: fish then sharks move (or eat); reproduction and energy applied. */
  @Override
  public CellState[][] getNextStates(CellState[][] currentStates) {
    initEnduringValues(currentStates);
    int rows = currentStates.length;
    int cols = currentStates[0].length;
    CellState[][] nextStates = new CellState[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        nextStates[r][c] = currentStates[r][c];
      }
    }
    Set<String> claimed = new HashSet<>();
    Set<String> eaten = new HashSet<>();

    List<int[]> fishPositions = new ArrayList<>();
    List<int[]> sharkPositions = new ArrayList<>();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (currentStates[r][c] == WaTorState.FISH) fishPositions.add(new int[] { r, c });
        else if (currentStates[r][c] == WaTorState.SHARK) sharkPositions.add(new int[] { r, c });
      }
    }
    Collections.shuffle(fishPositions);
    Collections.shuffle(sharkPositions);

    for (int[] pos : fishPositions) {
      int r = pos[0], c = pos[1];
      if (eaten.contains(key(r, c))) continue;
      List<int[]> neighborPositions = getNeighborPositions(rows, cols, r, c);
      moveIntoOpenSpot(r, c, currentStates, nextStates, false, claimed, neighborPositions);
    }
    for (int[] pos : sharkPositions) {
      int r = pos[0], c = pos[1];
      List<int[]> neighborPositions = getNeighborPositions(rows, cols, r, c);
      if (!trySharkEat(r, c, currentStates, nextStates, claimed, eaten, neighborPositions)) {
        moveIntoOpenSpot(r, c, currentStates, nextStates, true, claimed, neighborPositions);
      }
    }
    return nextStates;
  }

  /** Returns true if shark ate a fish (or died); false if no prey, so caller should call moveIntoOpenSpot. */
  private boolean trySharkEat(int r, int c, CellState[][] currentStates, CellState[][] nextStates,
      Set<String> claimed, Set<String> eaten, List<int[]> neighborPositions) {
    int energy = sharkEnergy[r][c] - 1;
    if (energy <= 0) {
      nextStates[r][c] = WaTorState.WATER;
      return true;
    }
    int energyFromFish = parameters != null && parameters.containsKey(SHARK_ENERGY_FROM_FISH)
        ? parameters.get(SHARK_ENERGY_FROM_FISH).intValue() : DEFAULT_ENERGY_FROM_EATING_FISH;
    List<int[]> fishSpots = getUnclaimedSpots(
        getNeighborPositionsForState(neighborPositions, WaTorState.FISH, currentStates), eaten);
    if (!fishSpots.isEmpty()) {
      int[] prey = fishSpots.get(rand.nextInt(fishSpots.size()));
      int pr = prey[0], pc = prey[1];
      eaten.add(key(pr, pc));
      claimed.add(key(pr, pc));
      int birthGens = DEFAULT_SHARK_BIRTH_GENS;
      int gens = sharkGenCount[r][c];
      nextStates[pr][pc] = WaTorState.SHARK;
      sharkEnergy[pr][pc] = energy + energyFromFish;
      sharkGenCount[pr][pc] = (gens >= birthGens) ? 0 : gens + 1;
      if (gens >= birthGens) {
        nextStates[r][c] = WaTorState.SHARK;
        sharkGenCount[r][c] = 0;
        sharkEnergy[r][c] = DEFAULT_SHARK_START_ENERGY;
      } else {
        nextStates[r][c] = WaTorState.WATER;
      }
      return true;
    }
    return false;
  }

  private void moveIntoOpenSpot(int r, int c, CellState[][] currentStates, CellState[][] nextStates,
      boolean isShark, Set<String> claimed, List<int[]> neighborPositions) {
    int birthGens = isShark ? DEFAULT_SHARK_BIRTH_GENS : DEFAULT_FISH_BIRTH_GENS;
    int gens = isShark ? sharkGenCount[r][c] : fishGenCount[r][c];
    int[][] genCountArray = isShark ? sharkGenCount : fishGenCount;
    CellState targetState = isShark ? WaTorState.SHARK : WaTorState.FISH;
    List<int[]> openSpots = getUnclaimedSpots(
        getNeighborPositionsForState(neighborPositions, WaTorState.WATER, currentStates), claimed);
    int sharkEnergyAfterStep = isShark ? sharkEnergy[r][c] - 1 : 0;
    if (openSpots.isEmpty()) {
      nextStates[r][c] = targetState;
      claimed.add(key(r, c));
      genCountArray[r][c] = gens + 1;
      if (isShark) sharkEnergy[r][c] = sharkEnergyAfterStep;
      return;
    }
    int[] newPosition = openSpots.get(rand.nextInt(openSpots.size()));
    int nr = newPosition[0], nc = newPosition[1];
    nextStates[nr][nc] = targetState;
    claimed.add(key(nr, nc));
    if (gens >= birthGens) {
      nextStates[r][c] = targetState;
      genCountArray[r][c] = 0;
      if (isShark) {
        sharkEnergy[r][c] = DEFAULT_SHARK_START_ENERGY;
        sharkEnergy[nr][nc] = sharkEnergyAfterStep;
      }
      genCountArray[nr][nc] = 0;
    } else {
      nextStates[r][c] = WaTorState.WATER;
      genCountArray[nr][nc] = gens + 1;
      if (isShark) sharkEnergy[nr][nc] = sharkEnergyAfterStep;
    }
  }

  private List<int[]> getUnclaimedSpots(List<int[]> spots, Set<String> claimed) {
    List<int[]> out = new ArrayList<>();
    for (int[] spot : spots) {
      if (!claimed.contains(key(spot[0], spot[1]))) out.add(spot);
    }
    return out;
  }

  @Override
  public Map<CellState, String> getStateColors() {
    return stateColorsFor(WaTorState.WATER, WaTorState.FISH, WaTorState.SHARK);
  }

  /** Maps 0=WATER, 1=FISH, 2=SHARK. */
  @Override
  public CellState fromInt(int value) {
    return WaTorState.fromInt(value);
  }

  @Override
  public CellState getNextStateInCycle(CellState current) {
    return WaTorState.getNextStateInCycle(current);
  }

  @Override
  public int fromState(CellState state) {
    return WaTorState.fromState(state);
  }
}
