package cellsociety.controller.services;

import static cellsociety.view.constants.BundleKeys.ERROR_FILE_READ;
import static cellsociety.view.constants.BundleKeys.ERROR_INVALID_CONFIG;
import static cellsociety.view.constants.BundleKeys.ERROR_INVALID_INPUT;
import static cellsociety.view.constants.BundleKeys.ERROR_INVALID_SLOT;
import static cellsociety.view.constants.BundleKeys.ERROR_INVALID_STATE;
import static cellsociety.view.constants.BundleKeys.ERROR_INVALID_XML;
import static cellsociety.view.constants.BundleKeys.ERROR_TITLE;
import static cellsociety.view.constants.BundleKeys.ERROR_UNEXPECTED;

import java.util.ResourceBundle;
import java.util.function.Supplier;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Centralized UI error handling for controller-level actions.
 *
 * <p>Purpose: map technical exceptions to user-facing messages and display them consistently.</p>
 */
public class ErrorHandler {

  private static final String FALLBACK_TITLE = "Error";
  private static final String FALLBACK_INVALID_CONFIG = "Invalid configuration file.";
  private static final String FALLBACK_INVALID_XML = "Invalid XML format.";
  private static final String FALLBACK_FILE_READ = "Could not read selected file.";
  private static final String FALLBACK_INVALID_INPUT = "Invalid input.";
  private static final String FALLBACK_INVALID_STATE = "Invalid application state.";
  private static final String FALLBACK_INVALID_SLOT = "Invalid simulation slot.";
  private static final String FALLBACK_UNEXPECTED = "Unexpected error.";

  private final Supplier<ResourceBundle> myBundleSupplier;

  public ErrorHandler(Supplier<ResourceBundle> bundleSupplier) {
    myBundleSupplier = bundleSupplier;
  }

  /**
   * Displays an error dialog based on the provided exception.
   *
   * @param exception exception to convert into user-facing text
   */
  public void handle(Exception exception) {
    ResourceBundle bundle = myBundleSupplier.get();
    String title = resolve(bundle, ERROR_TITLE, FALLBACK_TITLE);
    String message = mapMessage(bundle, exception);

    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(title);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private String mapMessage(ResourceBundle bundle, Exception exception) {
    if (exception instanceof ParserConfigurationException) {
      return resolve(bundle, ERROR_INVALID_CONFIG, FALLBACK_INVALID_CONFIG);
    }
    if (exception instanceof SAXException) {
      return resolve(bundle, ERROR_INVALID_XML, FALLBACK_INVALID_XML);
    }
    if (exception instanceof java.io.IOException) {
      return resolve(bundle, ERROR_FILE_READ, FALLBACK_FILE_READ);
    }
    if (exception instanceof IndexOutOfBoundsException) {
      return resolve(bundle, ERROR_INVALID_SLOT, FALLBACK_INVALID_SLOT);
    }
    if (exception instanceof IllegalArgumentException) {
      return resolve(bundle, ERROR_INVALID_INPUT, FALLBACK_INVALID_INPUT);
    }
    if (exception instanceof IllegalStateException) {
      return resolve(bundle, ERROR_INVALID_STATE, FALLBACK_INVALID_STATE);
    }
    return resolve(bundle, ERROR_UNEXPECTED, FALLBACK_UNEXPECTED);
  }

  private String resolve(ResourceBundle bundle, String key, String fallback) {
    if (bundle != null && bundle.containsKey(key)) {
      return bundle.getString(key);
    }
    return fallback;
  }
}
