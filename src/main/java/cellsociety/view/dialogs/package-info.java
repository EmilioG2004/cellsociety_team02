/**
 * Dialog and overlay components for user-facing workflows.
 *
 * Design goals:
 * Keep modal and overlay UX isolated from main view composition.
 * Expose simple methods that controllers can call without managing dialog internals.
 *
 * Contracts:
 * Dialog classes own JavaFX stage/pane composition and expose explicit show/close APIs.
 * Callers provide domain data objects, and dialogs render them without mutating models.
 *
 * @author Emilio Garcia
 * @author Team 02
 */
package cellsociety.view.dialogs;
