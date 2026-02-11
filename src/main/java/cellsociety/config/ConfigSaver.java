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
import static cellsociety.config.ConfigXmlConstants.*;

public class ConfigSaver {

  public void saveConfig(SimulationConfig config, File outFile)
      throws ParserConfigurationException, IOException {
    if (config == null) throw new IllegalArgumentException("config cannot be null");
    if (outFile == null) throw new IllegalArgumentException("outFile cannot be null");
  }
}
