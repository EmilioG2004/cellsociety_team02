package cellsociety.view.constants;

/**
 * Numeric constants for simulation defaults and UI layout.
 */
public final class SimulationUiConstants {
  private SimulationUiConstants() {}

  // Files
  public static final String XML_FILE_EXTENSION = "*.xml";
  public static final String DATA_FILE_FOLDER = System.getProperty("user.dir") + "/data";
  public static final String CONFIGS_FILE_FOLDER = DATA_FILE_FOLDER + "/configs";
  public static final String SAVES_FILE_FOLDER = DATA_FILE_FOLDER + "/saves";
  public static final String VERSION_KEY = "Version";

  // Simulation controller defaults
  public static final int MAX_SLOTS = 9;
  public static final int DEFAULT_SLOTS = 1;
  public static final int MAX_SPEED = 16;

  // SimulationView layout
  public static final double TITLE_FONT_SIZE = 28;
  public static final double MAIN_PADDING = 12;
  public static final double TOP_BOX_SPACING = 8;
  public static final double UPLOAD_ROW_PADDING_TOP = 6;
  public static final double UPLOAD_ROW_PADDING_RIGHT = 10;
  public static final double UPLOAD_ROW_PADDING_BOTTOM = 10;
  public static final double UPLOAD_ROW_PADDING_LEFT = 10;
  public static final double CONTROL_BAR_PADDING = 14;
  public static final double CONTROL_BAR_MIN_HEIGHT = 64;
  public static final double CENTER_CARD_PADDING = 18;
  public static final double CENTER_VERTICAL_PADDING = 18;
  public static final double SLOT_HEADER_PADDING_TOP = 4;
  public static final double SLOT_HEADER_PADDING_RIGHT = 6;
  public static final double SLOT_HEADER_PADDING_BOTTOM = 8;
  public static final double SLOT_HEADER_PADDING_LEFT = 6;
  public static final double MULTI_ROOT_PADDING = 4;

  // Multi-sim dashboard layout
  public static final int TILE_PREF_COLUMNS = 2;
  public static final double TILE_PREF_WIDTH = 420;
  public static final double TILE_PREF_HEIGHT = 420;
  public static final double TILE_GAP = 12;
  public static final double TILE_PADDING = 12;

  // General view spacing/padding
  public static final double SPACING_SMALL = 8;
  public static final double SPACING_MEDIUM = 12;
  public static final double PADDING_STANDARD = 12;
  public static final double PADDING_SETTINGS = 20;

  // Exception messages
  public static final String ERROR_MSG_NO_SIMULATION_LOADED = "No simulation loaded.";
  public static final String ERROR_MSG_NO_FILE_SELECTED = "No file selected.";
  public static final String ERROR_MSG_INVALID_SLOT_INDEX_PREFIX = "Invalid slot index: ";
  public static final String ERROR_MSG_NO_SIMULATION_FOR_SLOT_PREFIX = "No simulation loaded for slot ";
  public static final String ERROR_MSG_NO_SAVE_DESTINATION_SELECTED = "No Save Destination Selected.";
  public static final String ERROR_MSG_BASE_CONFIGURATION_REQUIRED = "Base configuration is required.";
  public static final String ERROR_MSG_GRID_SNAPSHOT_REQUIRED = "Grid snapshot is required.";
  public static final String ERROR_MSG_NO_NEIGHBORHOOD_SELECTED = "No neighborhood strategy selected.";
  public static final String ERROR_MSG_NO_GRID_EDGE_SELECTED = "No grid edge selected.";
  public static final String ERROR_MSG_UNSUPPORTED_GRID_EDGE_PREFIX = "Unsupported grid edge choice: ";
  public static final String ERROR_MSG_NO_CONFIGURATION_FOR_SLOT_PREFIX = "No configuration set for slot ";
  public static final String ERROR_MSG_NO_CONFIGURATION_SET = "No configuration set.";
}
