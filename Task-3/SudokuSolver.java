import java.util.Arrays;

/**
 * An optimized, production-ready Sudoku Solver that utilizes backtracking
 * combined with constraint tracking for maximum efficiency.
 */
public class SudokuSolver {

    private static final int GRID_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int EMPTY = 0;

    // Constraint arrays for O(1) lookups instead of scanning the grid repeatedly
    private final boolean[][] rowContains = new boolean[GRID_SIZE][GRID_SIZE + 1];
    private final boolean[][] colContains = new boolean[GRID_SIZE][GRID_SIZE + 1];
    private final boolean[][] boxContains = new boolean[GRID_SIZE][GRID_SIZE + 1];

    /**
     * Solves the provided Sudoku puzzle in-place.
     *
     * @param board A 9x9 matrix representing the Sudoku puzzle (0 for empty cells).
     * @return true if the puzzle was successfully solved, false if unsolvable.
     * @throws IllegalArgumentException if the input board is invalid.
     */
    public boolean solve(int[][] board) {
        validateInput(board);
        initializeConstraints(board);
        return backtrack(board, 0, 0);
    }

    /**
     * Recursive backtracking algorithm to fill the grid.
     */
    private boolean backtrack(int[][] board, int row, int col) {
        // If we reach the end of the row, move to the next row
        if (col == GRID_SIZE) {
            row++;
            col = 0;
        }

        // If we have traversed the entire board, the puzzle is solved
        if (row == GRID_SIZE) {
            return true;
        }

        // Skip cells that are already filled
        if (board[row][col] != EMPTY) {
            return backtrack(board, row, col + 1);
        }

        int boxIndex = getBoxIndex(row, col);

        // Try placing numbers from 1 to 9
        for (int num = 1; num <= GRID_SIZE; num++) {
            if (isValidPlacement(row, col, boxIndex, num)) {
                // Place the number and update constraints
                board[row][col] = num;
                rowContains[row][num] = true;
                colContains[col][num] = true;
                boxContains[boxIndex][num] = true;

                // Recurse to the next cell
                if (backtrack(board, row, col + 1)) {
                    return true;
                }

                // Backtrack: undo the placement if it didn't lead to a solution
                board[row][col] = EMPTY;
                rowContains[row][num] = false;
                colContains[col][num] = false;
                boxContains[boxIndex][num] = false;
            }
        }

        return false; // Triggers backtracking to previous cell
    }

    private boolean isValidPlacement(int row, int col, int boxIndex, int num) {
        return !rowContains[row][num] && 
               !colContains[col][num] && 
               !boxContains[boxIndex][num];
    }

    private int getBoxIndex(int row, int col) {
        return (row / SUBGRID_SIZE) * SUBGRID_SIZE + (col / SUBGRID_SIZE);
    }

    private void initializeConstraints(int[][] board) {
        for (int i = 0; i < GRID_SIZE; i++) {
            Arrays.fill(rowContains[i], false);
            Arrays.fill(colContains[i], false);
            Arrays.fill(boxContains[i], false);
        }

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int num = board[row][col];
                if (num != EMPTY) {
                    int boxIndex = getBoxIndex(row, col);
                    
                    // Check if the initial board itself has duplicate conflicts
                    if (rowContains[row][num] || colContains[col][num] || boxContains[boxIndex][num]) {
                        throw new IllegalArgumentException("The initial Sudoku board contains a conflict at row " + (row + 1) + ", col " + (col + 1));
                    }
                    
                    rowContains[row][num] = true;
                    colContains[col][num] = true;
                    boxContains[boxIndex][num] = true;
                }
            }
        }
    }

    private void validateInput(int[][] board) {
        if (board == null || board.length != GRID_SIZE) {
            throw new IllegalArgumentException("Sudoku board must be a 9x9 grid.");
        }
        for (int[] row : board) {
            if (row == null || row.length != GRID_SIZE) {
                throw new IllegalArgumentException("Sudoku board must be a 9x9 grid.");
            }
            for (int val : row) {
                if (val < 0 || val > GRID_SIZE) {
                    throw new IllegalArgumentException("Sudoku cell values must be between 0 and 9.");
                }
            }
        }
    }

    /**
     * Utility method to pretty-print the Sudoku grid with professional formatting.
     */
    public static void printBoard(int[][] board) {
        System.out.println("+-------+-------+-------+");
        for (int row = 0; row < GRID_SIZE; row++) {
            if (row > 0 && row % SUBGRID_SIZE == 0) {
                System.out.println("+-------+-------+-------+");
            }
            for (int col = 0; col < GRID_SIZE; col++) {
                if (col % SUBGRID_SIZE == 0) {
                    System.out.print("| ");
                }
                System.out.print(board[row][col] == EMPTY ? ". " : board[row][col] + " ");
            }
            System.out.println("|");
        }
        System.out.println("+-------+-------+-------+");
    }

    /**
     * Main method providing a test instance.
     */
    public static void main(String[] args) {
        // Example of an unsolved Hard Sudoku puzzle (0 represents empty cells)
        int[][] puzzle = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        System.out.println("--- Original Puzzle ---");
        printBoard(puzzle);

        SudokuSolver solver = new SudokuSolver();
        
        long startTime = System.nanoTime();
        boolean solved = solver.solve(puzzle);
        long endTime = System.nanoTime();

        if (solved) {
            System.out.println("\n--- Solved Successfully ---");
            printBoard(puzzle);
            System.out.printf("Execution Time: %.3f ms\n", (endTime - startTime) / 1e6);
        } else {
            System.out.println("\nThis Sudoku puzzle cannot be solved.");
        }
    }
}