package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.LangtonSDSRState;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Grid.RuleGrid;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;
import cellsociety.Model.NeighborStrategy.VonNeumannNeighborStrategy;

import java.util.Arrays;
import java.util.Map;

/**
 * SDSR loop:
 * - 9 states
 * - von Neumann neighborhood
 * - built from Langton's base rules + "undefined rule" extensions
 *
 * Transition key is (C, N, E, S, W) -> nextC, with rotational symmetry for the base rule list
 *
 * @author Ixa Sigler
 */
public class LangtonSDSRRule extends GameRule {

  private static final int STATES = 9;
  private static final int TABLE_SIZE = STATES * STATES * STATES * STATES * STATES; // 9^5 = 59049
  private static final int[] NEXT = buildTransitionTable();

  public LangtonSDSRRule(Map<String, Double> parameters) {
    super(parameters);
  }

  public LangtonSDSRRule() {
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

        int north = (r > 0) ? currentGrid.getCellState(r - 1, c).toInt() : 0;
        int east  = (c < cols - 1) ? currentGrid.getCellState(r, c + 1).toInt() : 0;
        int south = (r < rows - 1) ? currentGrid.getCellState(r + 1, c).toInt() : 0;
        int west  = (c > 0) ? currentGrid.getCellState(r, c - 1).toInt() : 0;

        int nextVal = NEXT[idx(center, north, east, south, west)];
        next.setCellState(r, c, LangtonSDSRState.fromInt(nextVal));
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
    return LangtonSDSRState.fromInt(value);
  }

  private static int idx(int c, int n, int e, int s, int w) {
    return ((((c * STATES + n) * STATES + e) * STATES + s) * STATES + w);
  }

  private static boolean inSheath(int n, int e, int s, int w) {
    int f = 0;
    if (isSheathState(n)) f++;
    if (isSheathState(e)) f++;
    if (isSheathState(s)) f++;
    if (isSheathState(w)) f++;
    return f > 1;
  }

  private static boolean isSheathState(int x) {
    return x == 1 || x == 2 || x == 4 || x == 6 || x == 7;
  }

  private static int[] buildTransitionTable() {
    int[] table = new int[TABLE_SIZE];
    Arrays.fill(table, -1);

    loadLangtonBaseRules(table);
    fillUndefinedRulesWithSDSR(table);
    applySDSROverrides(table);
    finalizeUndefinedRules(table);

    return table;
  }

  private static void loadLangtonBaseRules(int[] table) {
    // Langton base rules
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

    // rotational symmetry
    for (String rule : rules) {
      int c = rule.charAt(0) - '0';
      int n = rule.charAt(1) - '0';
      int e = rule.charAt(2) - '0';
      int s = rule.charAt(3) - '0';
      int w = rule.charAt(4) - '0';
      int d = rule.charAt(5) - '0';

      table[idx(c, n, e, s, w)] = d;
      table[idx(c, e, s, w, n)] = d;
      table[idx(c, s, w, n, e)] = d;
      table[idx(c, w, n, e, s)] = d;
    }
  }

  private static void fillUndefinedRulesWithSDSR(int[] table) {
    forEachNeighborhood((a, b, c, d, e) -> {
      int index = idx(a, b, c, d, e);
      if (table[index] != -1) return;

      Integer result = sdsrUndefinedRule(a, b, c, d, e);
      if (result != null) {
        table[index] = result;
      }
    });
  }

  // encodes SDSR "undefined rule" logic
  private static Integer sdsrUndefinedRule(int a, int b, int c, int d, int e) {

    if (a == 8) return 0;

    boolean neighborHas8 = (b == 8) || (c == 8) || (d == 8) || (e == 8);
    if (neighborHas8) {
      return ruleWhenNeighborHasDissolve(a, b, c, d, e);
    }

    return ruleWhenNoDissolveNearby(a, b, c, d, e);
  }

  private static Integer ruleWhenNeighborHasDissolve(int a, int b, int c, int d, int e) {
    switch (a) {
      case 1: {
        boolean has2to7 =
            ((b >= 2 && b <= 7) || (c >= 2 && c <= 7) || (d >= 2 && d <= 7) || (e >= 2 && e <= 7));
        return has2to7 ? 8 : a;
      }
      case 5:
        return 0;
      case 7:
        return 1;
      default:
        return 8;
    }
  }

  private static Integer ruleWhenNoDissolveNearby(int a, int b, int c, int d, int e) {
    switch (a) {
      case 0:
        return ((b == 1 || c == 1 || d == 1 || e == 1) && inSheath(b, c, d, e)) ? 1 : 0;

      case 1:
        if ((b == 7 || c == 7 || d == 7 || e == 7) && inSheath(b, c, d, e)) return 7;
        if ((b == 6 || c == 6 || d == 6 || e == 6) && inSheath(b, c, d, e)) return 6;
        if ((b == 4 || c == 4 || d == 4 || e == 4) && inSheath(b, c, d, e)) return 4;
        return null;

      case 2:
        if (b == 3 || c == 3 || d == 3 || e == 3) return 1;
        if (b == 2 || c == 2 || d == 2 || e == 2) return 2;
        return null;

      case 7:
        if ((b == 0 || c == 0 || d == 0 || e == 0) && inSheath(b, c, d, e)) return 0;
        return null;

      default:
        return null;
    }
  }

  private static void applySDSROverrides(int[] table) {
    table[idx(1, 1, 1, 5, 2)] = 8;
    table[idx(1, 1, 5, 2, 1)] = 8;
    table[idx(1, 5, 2, 1, 1)] = 8;
    table[idx(1, 2, 1, 1, 5)] = 8;
  }

  private static void finalizeUndefinedRules(int[] table) {
    for (int i = 0; i < table.length; i++) {
      if (table[i] == -1) table[i] = 8;
    }
  }

  @FunctionalInterface
  private interface NeighborhoodConsumer {
    void accept(int a, int b, int c, int d, int e);
  }

  private static void forEachNeighborhood(NeighborhoodConsumer action) {
    for (int a = 0; a < STATES; a++) {
      for (int b = 0; b < STATES; b++) {
        for (int c = 0; c < STATES; c++) {
          for (int d = 0; d < STATES; d++) {
            for (int e = 0; e < STATES; e++) {
              action.accept(a, b, c, d, e);
            }
          }
        }
      }
    }
  }
}