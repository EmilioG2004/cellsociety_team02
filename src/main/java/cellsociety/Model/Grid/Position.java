package cellsociety.Model.Grid;

/**
 * Immutable (row, col) position on a grid. Used as a key for neighbor maps.
 *
 * @author Michael Lei
 */
public record Position(int row, int col) {}
