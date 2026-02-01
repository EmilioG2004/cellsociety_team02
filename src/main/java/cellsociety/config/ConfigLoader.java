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

  public SimulationConfig loadConfig(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
    Document xmlDocument = DocumentBuilderFactory
      .newInstance()
      .newDocumentBuilder()
      .parse(xmlFile);


    int[] gridSize = calculateGridSize(xmlDocument);
    int rows = gridSize[0];
    int cols = gridSize[1];

    return new SimulationConfig(
      "",
      "",
      "",
      "",
      rows,
      cols,
      new int[rows][cols],
      new HashMap<>()
    );
  }


  private int[] calculateGridSize(Document xmlDocument) {
    try {
      Element root = xmlDocument.getDocumentElement();
      Element grid = (Element) root.getElementsByTagName("grid").item(0);

      int rows = Integer.parseInt(grid.getAttribute("rows"));
      int cols = Integer.parseInt(grid.getAttribute("cols"));

      return new int[]{rows, cols};
    }
    catch (Exception e) {
      System.err.println("Invalid grid configuration");
      return new int[]{0, 0};
    }
  }
}
