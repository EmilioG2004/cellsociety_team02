package cellsociety.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

    return new SimulationConfig(
      "",
      title,
      author,
      description,
      rows,
      cols,
      new int[rows][cols],
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
}
