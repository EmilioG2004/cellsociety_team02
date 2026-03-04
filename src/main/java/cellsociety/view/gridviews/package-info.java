/**
 * Grid-rendering abstractions and concrete JavaFX renderers.
 *
 * Design goals:
 * Provide a stable rendering contract while allowing multiple renderer implementations.
 * Keep rendering-specific logic separate from simulation stepping and persistence.
 *
 * Contracts:
 * Implementations accept read-only grid snapshots and expose JavaFX nodes for embedding.
 * Public renderer methods are side-effected only on view state, not simulation data.
 *
 * @author Emilio Garcia
 * @author Team 02
 */
package cellsociety.view.gridviews;
