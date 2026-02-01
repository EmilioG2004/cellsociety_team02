package cellsociety.config;

import cellsociety.Main;
import com.sun.javafx.geom.Vec2d;
import java.awt.Dimension;
import java.io.File;
import java.util.Vector;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Feel free to completely change this code or delete it entirely.
 */
public class ConfigLoader {
  public Vec2d calculateGridSize(File xmlFile) {
    try {
      Document xmlDocument = DocumentBuilderFactory
          .newInstance()
          .newDocumentBuilder()
          .parse(xmlFile);

      Element root = xmlDocument.getDocumentElement();
      Element grid = (Element) root.getElementsByTagName("grid").item(0);

      int rows = Integer.parseInt(grid.getAttribute("rows"));
      int cols = Integer.parseInt(grid.getAttribute("cols"));

      return new Vec2d(cols, rows);
    }
    catch (Exception e) {
      System.err.println("Invalid grid configuration");
      return new Vec2d(0, 0);
    }
  }
}
