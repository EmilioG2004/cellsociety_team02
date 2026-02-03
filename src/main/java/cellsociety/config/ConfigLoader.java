package cellsociety.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Feel free to completely change this code or delete it entirely.
 */
public class ConfigLoader {

  private static final String CONFIG_TITLE = "title";
  private static final String CONFIG_AUTHOR = "author";
  private static final String CONFIG_DESCRIPTION = "description";


  public SimulationConfig loadConfig(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
    Document xmlDocument = DocumentBuilderFactory
      .newInstance()
      .newDocumentBuilder()
      .parse(xmlFile);

    Element metadata = getMetadata(xmlDocument);
    String title = getChildText(metadata, CONFIG_TITLE);
    String author = getChildText(metadata, CONFIG_AUTHOR);
    String description = getChildText(metadata, CONFIG_DESCRIPTION);

    int[] gridSize = getGridSize(xmlDocument);
    int rows = gridSize[0];
    int cols = gridSize[1];

    int[][] states = getStates(xmlDocument, rows, cols);

    return new SimulationConfig(
      "",
      title,
      author,
      description,
      rows,
      cols,
      states,
      new HashMap<>()
    );
  }

  private Element getMetadata(Document xmlDocument) {
    Element root = xmlDocument.getDocumentElement();
    Element metadata = (Element) root.getElementsByTagName("metadata").item(0);
    if (metadata == null) {
      throw new IllegalArgumentException("Missing <metadata> element");
    }
    return metadata;
  }

  private String getChildText(Element parent, String tagName) {
    Element child = (Element) parent.getElementsByTagName(tagName).item(0);
    if (child == null) return "";
    return child.getTextContent().trim();
  }

  private int[] getGridSize(Document xmlDocument) {
    Element root = xmlDocument.getDocumentElement();
    Element grid = (Element) root.getElementsByTagName("grid").item(0);
    if (grid == null) throw new IllegalArgumentException("Missing <grid> element");

    String rowsStr = grid.getAttribute("rows");
    String colsStr = grid.getAttribute("cols");
    if (rowsStr.isBlank() || colsStr.isBlank()) {
      throw new IllegalArgumentException("Missing grid rows/cols attributes");
    }

    int rows = Integer.parseInt(rowsStr);
    int cols = Integer.parseInt(colsStr);
    if (rows <= 0 || cols <= 0) {
      throw new IllegalArgumentException("Grid rows/cols must be positive");
    }

    return new int[]{rows, cols};
  }

  private int[][] getStates(Document xmlDocument, int rows, int cols) {
    Element root = xmlDocument.getDocumentElement();
    Element grid = (Element) root.getElementsByTagName("grid").item(0);
    if (grid == null) throw new IllegalArgumentException("Missing <grid> element");

    Element statesElement = (Element) grid.getElementsByTagName("states").item(0);
    if (statesElement == null) {
      throw new IllegalArgumentException("Missing <states> element");
    }

    String rawText = statesElement.getTextContent();
    List<String> lines = nonEmptyLines(rawText);
    return iterateThroughRows(lines, rows, cols);
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
          "Expected " + rows + " rows in <states>, but got " + lines.size());
    }

    int[][] states = new int[rows][cols];
    for (int r = 0; r < rows; r++) {
      states[r] = parseRow(lines.get(r), cols, r);
    }
    return states;
  }

  private int[] parseRow(String line, int cols, int currentRow) {
    String[] tokens = line.split(",");
    validateTokens(tokens, cols, currentRow);

    int[] row = new int[cols];
    for (int c = 0; c < cols; c++) {
      try {
        row[c] = Integer.parseInt(tokens[c].trim());
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
            "Invalid state value at row " + currentRow + ", col " + c);
      }
    }
    return row;
  }

  private void validateTokens(String[] tokens, int cols, int currentRow) {
    if (tokens.length != cols) {
      throw new IllegalArgumentException(
          "Row " + currentRow + " has " + tokens.length +
              " columns, expected " + cols);
    }
  }
}
