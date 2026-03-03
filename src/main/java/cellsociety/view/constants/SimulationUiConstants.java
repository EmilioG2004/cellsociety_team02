package cellsociety.view.constants;

/**
 * Numeric constants for simulation defaults and UI layout.
 */
public final class SimulationUiConstants {
  private SimulationUiConstants() {}

  // Files
  public static final String XML_FILE_EXTENSION = "*.xml";
  public static final String DATA_FILE_FOLDER = System.getProperty("user.dir") + "/data";
  public static final String VERSION_KEY = "Version";

  // Simulation controller defaults
  public static final int MAX_SLOTS = 9;
  public static final int DEFAULT_SLOTS = 1;
  public static final int DOUBLE_VALUE = 2;

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
}
