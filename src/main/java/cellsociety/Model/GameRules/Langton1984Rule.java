package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.Langton1984State;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Grid.RuleGrid;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;
import cellsociety.Model.NeighborStrategy.VonNeumannNeighborStrategy;

import java.util.List;
import java.util.Map;

/**
 * Langton's self-reproducing loops (1984): 8 states, von Neumann neighborhood, rotational symmetry.
 *
 * Transition key is (C, N, E, S, W) -> nextC.
 * All unspecified transitions go to 0 (empty).
 * @author Ixa Sigler
 */
public class Langton1984Rule extends GameRule {

  private static final int STATES = 8;
  private static final int TABLE_SIZE = STATES * STATES * STATES * STATES * STATES; // 8^5 = 32768
  private static final int[] NEXT = buildTransitionTable();

  public Langton1984Rule(Map<String, Double> parameters) {
    super(parameters);
  }

  public Langton1984Rule() {
    this(Map.of());
  }

  @Override
  public Grid getNextGrid(RuleGrid currentGrid) {
    Grid next = currentGrid.deepCopy();

    int rows = currentGrid.getRows();
    int cols = currentGrid.getCols();

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        int center = currentGrid.getCellState(r, c).toInt();

        // neighbors in n,e,s,w order
        List<CellState> n = currentGrid.getNeighbors(r, c);
        int north = n.get(0).toInt();
        int east  = n.get(1).toInt();
        int south = n.get(2).toInt();
        int west  = n.get(3).toInt();

        int nextVal = NEXT[idx(center, north, east, south, west)];
        next.setCellState(r, c, Langton1984State.fromInt(nextVal));
      }
    }
    return next;
  }

  @Override
  public NeighborStrategy getDefaultNeighborStrategy() {
    return new VonNeumannNeighborStrategy();
  }

  @Override
  public CellState fromInt(int value) {
    return Langton1984State.fromInt(value);
  }

  private static int idx(int c, int n, int e, int s, int w) {
    return ((((c * STATES + n) * STATES + e) * STATES + s) * STATES + w);
  }

  private static int[] buildTransitionTable() {
    int[] table = new int[TABLE_SIZE]; // default 0 everywhere

    // rules encoded as "cneswd" with rotational symmetry
    String[] rules = new String[] {
        "000000",
        "000012",
        "000020",
        "000030",
        "000050",
        "000063",
        "000071",
        "000112",
        "000122",
        "000132",
        "000212",
        "000220",
        "000230",
        "000262",
        "000272",
        "000320",
        "000525",
        "000622",
        "000722",
        "001022",
        "001120",
        "002020",
        "002030",
        "002050",
        "002125",
        "002220",
        "002322",
        "005222",
        "012321",
        "012421",
        "012525",
        "012621",
        "012721",
        "012751",
        "014221",
        "014321",
        "014421",
        "014721",
        "016251",
        "017221",
        "017255",
        "017521",
        "017621",
        "017721",
        "025271",
        "100011",
        "100061",
        "100077",
        "100111",
        "100121",
        "100211",
        "100244",
        "100277",
        "100511",
        "101011",
        "101111",
        "101244",
        "101277",
        "102026",
        "102121",
        "102211",
        "102244",
        "102263",
        "102277",
        "102327",
        "102424",
        "102626",
        "102644",
        "102677",
        "102710",
        "102727",
        "105427",
        "111121",
        "111221",
        "111244",
        "111251",
        "111261",
        "111277",
        "111522",
        "112121",
        "112221",
        "112244",
        "112251",
        "112277",
        "112321",
        "112424",
        "112621",
        "112727",
        "113221",
        "122244",
        "122277",
        "122434",
        "122547",
        "123244",
        "123277",
        "124255",
        "124267",
        "125275",
        "200012",
        "200022",
        "200042",
        "200071",
        "200122",
        "200152",
        "200212",
        "200222",
        "200232",
        "200242",
        "200250",
        "200262",
        "200272",
        "200326",
        "200423",
        "200517",
        "200522",
        "200575",
        "200722",
        "201022",
        "201122",
        "201222",
        "201422",
        "201722",
        "202022",
        "202032",
        "202052",
        "202073",
        "202122",
        "202152",
        "202212",
        "202222",
        "202272",
        "202321",
        "202422",
        "202452",
        "202520",
        "202552",
        "202622",
        "202722",
        "203122",
        "203216",
        "203226",
        "203422",
        "204222",
        "205122",
        "205212",
        "205222",
        "205521",
        "205725",
        "206222",
        "206722",
        "207122",
        "207222",
        "207422",
        "207722",
        "211222",
        "211261",
        "212222",
        "212242",
        "212262",
        "212272",
        "214222",
        "215222",
        "216222",
        "217222",
        "222272",
        "222442",
        "222462",
        "222762",
        "222772",
        "300013",
        "300022",
        "300041",
        "300076",
        "300123",
        "300421",
        "300622",
        "301021",
        "301220",
        "302511",
        "401120",
        "401220",
        "401250",
        "402120",
        "402221",
        "402326",
        "402520",
        "403221",
        "500022",
        "500215",
        "500225",
        "500232",
        "500272",
        "500520",
        "502022",
        "502122",
        "502152",
        "502220",
        "502244",
        "502722",
        "512122",
        "512220",
        "512422",
        "512722",
        "600011",
        "600021",
        "602120",
        "612125",
        "612131",
        "612225",
        "700077",
        "701120",
        "701220",
        "701250",
        "702120",
        "702221",
        "702251",
        "702321",
        "702525",
        "702720"
    };

    for (String rule : rules) {
      // "cneswc"
      int c = rule.charAt(0) - '0';
      int n = rule.charAt(1) - '0';
      int e = rule.charAt(2) - '0';
      int s = rule.charAt(3) - '0';
      int w = rule.charAt(4) - '0';
      int d = rule.charAt(5) - '0';

      // apply rotational symmetry:
      table[idx(c, n, e, s, w)] = d;
      table[idx(c, e, s, w, n)] = d;
      table[idx(c, s, w, n, e)] = d;
      table[idx(c, w, n, e, s)] = d;
    }

    return table;
  }
}