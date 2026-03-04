/**
 * Public view API for the simulator application.
 *
 * Design goals:
 * Keep rendering and UI composition separate from simulation rules and controller logic.
 * Allow controllers to evolve without forcing view implementations to expose internal state.
 * Support future extension with additional dialogs, renderers, and themes.
 *
 * Contracts:
 * Controllers drive this package through stable methods on view classes and interfaces.
 * View classes return JavaFX nodes and consume immutable or read-only model data where possible.
 * Errors are surfaced as runtime exceptions for invalid UI usage, such as missing required data.
 * Communication is callback-based for user actions and object-based for simulation snapshots.
 *
 * @author Emilio Garcia
 * @author Michael Lei
 */
package cellsociety.view;
