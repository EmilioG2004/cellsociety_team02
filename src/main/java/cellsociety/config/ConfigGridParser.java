package cellsociety.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static cellsociety.config.ConfigXmlConstants.*;

public class ConfigGridParser {

  public int[] parseGridSize(Document xmlDocument) {
    Element grid = getGridElement(xmlDocument);

    String rowsStr = grid.getAttribute(CONFIG_ROWS);
    String colsStr = grid.getAttribute(CONFIG_COLUMNS);
    if (rowsStr.isBlank() || colsStr.isBlank()) {
      throw new IllegalArgumentException("Missing grid rows/cols attributes");
    }

    int rows = Integer.parseInt(rowsStr);
    int cols = Integer.parseInt(colsStr);
    if (rows <= 0 || cols <= 0) {
      throw new IllegalArgumentException("Grid rows/cols must be positive");
    }
    return new int[] { rows, cols };
  }

  public int[][] parseInitialStates(Document xmlDocument, int rows, int cols) {
    Element gridEl = getGridElement(xmlDocument);

    Element statesElement = getFirstDirectChild(gridEl, CONFIG_STATES);

    int[][] base;
    if (statesElement != null) {
      base = parseExplicitStates(statesElement, rows, cols);
    } else {
      base = parseRandomByCountStates(gridEl, rows, cols);
    }

    applyPatterns(gridEl, base, rows, cols);

    return base;
  }

  private int[][] parseRandomByCountStates(Element grid, int rows, int cols) {
    Element randomElement = getFirstDirectChild(grid, CONFIG_RANDOM_BY_COUNT);
    if (randomElement == null) {
      throw new IllegalArgumentException(
          "Missing <" + CONFIG_STATES + "> and no <" + CONFIG_RANDOM_BY_COUNT + "> provided."
      );
    }

    int totalCells = rows * cols;
    CountsSpec spec = getCountsSpec(randomElement);

    int fixedSum = sumInts(spec.fixedCounts);
    if (fixedSum > totalCells) {
      throw new IllegalArgumentException(
          "Specified fixed state counts (" + fixedSum + ") exceed total grid cells (" + totalCells + ")"
      );
    }

    int remaining = totalCells - fixedSum;

    Map<Integer, Integer> counts = new HashMap<>(spec.fixedCounts);
    if (!spec.proportions.isEmpty() && remaining > 0) {
      allocateProportionsIntoCounts(counts, spec.proportions, remaining);
    }

    fillRemainder(counts, totalCells);
    return buildGridFromStateCounts(counts, rows, cols);
  }

  /** Holds parsed <state> info before turning proportions into integer counts */
  private static class CountsSpec {
    final Map<Integer, Integer> fixedCounts = new HashMap<>();
    final Map<Integer, Double> proportions = new HashMap<>();
  }

  /** Reads all <state> nodes into fixedCounts and proportions */
  private CountsSpec getCountsSpec(Element randomElement) {
    CountsSpec spec = new CountsSpec();
    var stateNodes = randomElement.getElementsByTagName(CONFIG_RANDOM_STATE);

    for (int i = 0; i < stateNodes.getLength(); i++) {
      Element stateEl = (Element) stateNodes.item(i);

      int id = parseNonNegativeInt(stateEl, CONFIG_ID_ATTR);

      String countRaw = stateEl.getAttribute(CONFIG_COUNT_ATTR).trim();
      String propRaw  = stateEl.getAttribute(CONFIG_PROPORTION_ATTR).trim();

      boolean hasCount = !countRaw.isEmpty();
      boolean hasProp  = !propRaw.isEmpty();

      if (hasCount == hasProp) {
        throw new IllegalArgumentException(
            "<state id=\"" + id + "\"> must specify exactly one of '" +
                CONFIG_COUNT_ATTR + "' or '" + CONFIG_PROPORTION_ATTR + "'"
        );
      }

      if (spec.fixedCounts.containsKey(id) || spec.proportions.containsKey(id)) {
        throw new IllegalArgumentException("Duplicate state id: " + id);
      }

      if (hasCount) {
        spec.fixedCounts.put(id, parseNonNegativeInt(stateEl, CONFIG_COUNT_ATTR));
      } else {
        double p = parseNonNegativeDouble(stateEl, CONFIG_PROPORTION_ATTR);
        if (p > 1.0) {
          throw new IllegalArgumentException("Proportion must be between 0.0 and 1.0 for state id: " + id);
        }
        spec.proportions.put(id, p);
      }
    }
    return spec;
  }

  /**
   * Turns proportion weights into integer counts over 'remainingCells' and puts them into 'counts'
   */
  private void allocateProportionsIntoCounts(Map<Integer, Integer> counts,
      Map<Integer, Double> proportions,
      int remainingCells) {

    double sumProps = 0.0;
    for (double p : proportions.values()) sumProps += p;

    if (sumProps > 1.0) {
      throw new IllegalArgumentException("Sum of proportions (" + sumProps + ") exceeds 1.0");
    }

    for (Map.Entry<Integer, Double> e : proportions.entrySet()) {
      int id = e.getKey();
      double p = e.getValue();

      if (p < 0.0 || p > 1.0) {
        throw new IllegalArgumentException("Invalid proportion for state " + id + ": " + p);
      }

      int count = (int) Math.floor(p * remainingCells);
      counts.put(id, count);
    }
  }

  private int sumInts(Map<Integer, Integer> m) {
    int sum = 0;
    for (int v : m.values()) sum += v;
    return sum;
  }

  private Element getGridElement(Document xmlDocument) {
    Element root = xmlDocument.getDocumentElement();
    Element grid = (Element) root.getElementsByTagName(CONFIG_GRID).item(0);
    if (grid == null) {
      throw new IllegalArgumentException("Missing <" + CONFIG_GRID + "> element");
    }
    return grid;
  }

  private int[][] parseExplicitStates(Element statesElement, int rows, int cols) {
    String rawText = statesElement.getTextContent();
    List<String> lines = nonEmptyLines(rawText);
    return iterateThroughRows(lines, rows, cols);
  }

  private void applyPatterns(Element gridEl, int[][] grid, int rows, int cols) {
    NodeList patternNodes = gridEl.getElementsByTagName(CONFIG_PATTERN);
    for (int i = 0; i < patternNodes.getLength(); i++) {
      Element patternEl = (Element) patternNodes.item(i);

      int startRow = parseNonNegativeInt(patternEl, CONFIG_ROWS);
      int startCol = parseNonNegativeInt(patternEl, CONFIG_COLUMNS);

      // pattern must contain its own <states> direct child
      Element patternStatesEl = getFirstDirectChild(patternEl, CONFIG_STATES);
      if (patternStatesEl == null) {
        throw new IllegalArgumentException("Missing <" + CONFIG_STATES + "> inside <" + CONFIG_PATTERN + ">");
      }

      List<String> lines = nonEmptyLines(patternStatesEl.getTextContent());
      if (lines.isEmpty()) continue;

      int patternCols = lines.get(0).split(",").length;

      if (startRow + lines.size() > rows || startCol + patternCols > cols) {
        throw new IllegalArgumentException(
            "Pattern at (" + startRow + "," + startCol + ") with size " +
                lines.size() + "x" + patternCols + " exceeds grid bounds " + rows + "x" + cols
        );
      }

      // apply pattern
      for (int pr = 0; pr < lines.size(); pr++) {
        int[] patternRow = parseRow(lines.get(pr), patternCols, pr);
        for (int pc = 0; pc < patternCols; pc++) {
          grid[startRow + pr][startCol + pc] = patternRow[pc];
        }
      }
    }
  }

  private Element getFirstDirectChild(Element parent, String tagName) {
    NodeList children = parent.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node n = children.item(i);
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        Element el = (Element) n;
        if (tagName.equals(el.getTagName())) {
          return el;
        }
      }
    }
    return null;
  }

  private int parseNonNegativeInt(Element el, String attr) {
    String raw = el.getAttribute(attr).trim();
    if (raw.isEmpty()) throw new IllegalArgumentException("<state> missing '" + attr + "' attribute");
    try {
      int value = Integer.parseInt(raw);
      if (value < 0) throw new IllegalArgumentException();
      return value;
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid non-negative integer for '" + attr + "': " + raw);
    }
  }

  private double parseNonNegativeDouble(Element el, String attr) {
    String raw = el.getAttribute(attr).trim();
    if (raw.isEmpty()) throw new IllegalArgumentException("<state> missing '" + attr + "' attribute");
    try {
      double value = Double.parseDouble(raw);
      if (value < 0) throw new IllegalArgumentException();
      return value;
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid non-negative double for '" + attr + "': " + raw);
    }
  }

  private void fillRemainder(Map<Integer, Integer> counts, int totalCells) {
    int sum = 0;
    for (int c : counts.values()) sum += c;

    if (sum > totalCells) {
      throw new IllegalArgumentException(
          "Specified state counts (" + sum + ") exceed total grid cells (" + totalCells + ")"
      );
    }

    if (sum < totalCells) {
      counts.put(0, counts.getOrDefault(0, 0) + (totalCells - sum));
    }
  }

  private int[][] buildGridFromStateCounts(Map<Integer, Integer> counts, int rows, int cols) {
    int totalCells = rows * cols;
    List<Integer> values = new ArrayList<>(totalCells);

    for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
      int stateId = entry.getKey();
      int count = entry.getValue();
      for (int i = 0; i < count; i++) values.add(stateId);
    }

    if (values.size() != totalCells) {
      throw new IllegalArgumentException(
          "State counts do not match grid size. Expected " + totalCells + " but got " + values.size()
      );
    }

    Collections.shuffle(values);

    int[][] grid = new int[rows][cols];
    int index = 0;
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        grid[r][c] = values.get(index++);
      }
    }
    return grid;
  }

  private List<String> nonEmptyLines(String rawText) {
    List<String> rows = new ArrayList<>();
    for (String line : rawText.split("\\R")) {
      line = line.trim();
      if (!line.isEmpty()) rows.add(line);
    }
    return rows;
  }

  private int[][] iterateThroughRows(List<String> lines, int rows, int cols) {
    if (lines.size() != rows) {
      throw new IllegalArgumentException(
          "Expected " + rows + " rows in <" + CONFIG_STATES + ">, but got " + lines.size()
      );
    }

    int[][] states = new int[rows][cols];
    for (int r = 0; r < rows; r++) {
      states[r] = parseRow(lines.get(r), cols, r);
    }
    return states;
  }

  private int[] parseRow(String line, int cols, int currentRow) {
    String[] tokens = line.split(",");
    if (tokens.length != cols) {
      throw new IllegalArgumentException(
          "Row " + currentRow + " has " + tokens.length + " columns, expected " + cols
      );
    }

    int[] row = new int[cols];
    for (int c = 0; c < cols; c++) {
      try {
        row[c] = Integer.parseInt(tokens[c].trim());
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid state value at row " + currentRow + ", col " + c);
      }
    }
    return row;
  }
}
