package cellsociety;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Feel free to completely change this code or delete it entirely.
 */
public class ConfigLoader {
    // kind of data files to look for
    public static final String DATA_FILE_EXTENSION = "*.xml";
    // default to start in the data folder to make it easy on the user to find
    public static final String DATA_FILE_FOLDER = System.getProperty("user.dir") + "/data";
    // NOTE: make ONE chooser since generally accepted behavior is that it remembers where user left it last
    private final static FileChooser FILE_CHOOSER = makeChooser(DATA_FILE_EXTENSION);
    // internal configuration file
    public static final String INTERNAL_CONFIGURATION = "cellsociety.Version";
}
