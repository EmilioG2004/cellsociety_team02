package cellsociety.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static cellsociety.config.ConfigXmlConstants.*;

/**
 * Responsible ONLY for parsing the <grid> section
 */
public class ConfigGridParser {

  /**
   * @return int[]{rows, cols}
   */
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

  /**
   * Parses initial cell states from the grid section: either explicit {@code <states>} CSV or {@code <randomByCount>}.
   *
   * @param xmlDocument the parsed XML document
   * @param rows        expected number of rows
   * @param cols        expected number of columns
   * @return a rows x cols matrix of integer state codes
   * @throws IllegalArgumentException if grid/states are missing or dimensions do not match
   */
  public int[][] parseInitialStates(Document xmlDocument, int rows, int cols) {
    Element grid = getGridElement(xmlDocument);

    Element statesElement = (Element) grid.getElementsByTagName(CONFIG_STATES).item(0);
    if (statesElement != null) {
      return parseExplicitStates(statesElement, rows, cols);
    }

    return parseRandomByCountStates(grid, rows, cols);
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

  private int[][] parseRandomByCountStates(Element grid, int rows, int cols) {
    Element randomElement = (Element) grid.getElementsByTagName(CONFIG_RANDOM_BY_COUNT).item(0);
    if (randomElement == null) {
      throw new IllegalArgumentException(
          "Missing <" + CONFIG_STATES + "> and no <" + CONFIG_RANDOM_BY_COUNT + "> provided."
      );
    }

    Map<Integer, Integer> counts = parseStateCounts(randomElement);

    int totalCells = rows * cols;
    fillRemainder(counts, totalCells);

    return buildGridFromStateCounts(counts, rows, cols);
  }

  private Map<Integer, Integer> parseStateCounts(Element randomElement) {
    Map<Integer, Integer> counts = new HashMap<>();

    var stateNodes = randomElement.getElementsByTagName(CONFIG_RANDOM_STATE);

    for (int i = 0; i < stateNodes.getLength(); i++) {
      Element stateEl = (Element) stateNodes.item(i);

      int id = parseNonNegativeInt(stateEl, CONFIG_ID_ATTR);
      int count = parseNonNegativeInt(stateEl, CONFIG_COUNT_ATTR);

      if (counts.containsKey(id)) {
        throw new IllegalArgumentException("Duplicate state id: " + id);
      }
      counts.put(id, count);
    }

    return counts;
  }

  private int parseNonNegativeInt(Element el, String attr) {
    String raw = el.getAttribute(attr).trim();
    if (raw.isEmpty()) {
      throw new IllegalArgumentException("<state> missing '" + attr + "' attribute");
    }

    try {
      int value = Integer.parseInt(raw);
      if (value < 0) throw new IllegalArgumentException();
      return value;
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid non-negative integer for '" + attr + "': " + raw);
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
