package sudoku;

import org.junit.jupiter.api.Test;
import sudoku.dancing.DancingLinksSudokuSolver;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SudokuSolverTest {

    /** Knuth's classic "world's hardest sudoku" puzzle — many hard puzzles converge here. */
    private static final int[][] HARD_PUZZLE = {
            {8, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 3, 6, 0, 0, 0, 0, 0},
            {0, 7, 0, 0, 9, 0, 2, 0, 0},
            {0, 5, 0, 0, 0, 7, 0, 0, 0},
            {0, 0, 0, 0, 4, 5, 7, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 3, 0},
            {0, 0, 1, 0, 0, 0, 0, 6, 8},
            {0, 0, 8, 5, 0, 0, 0, 1, 0},
            {0, 9, 0, 0, 0, 0, 4, 0, 0}
    };

    @Test
    void backtrackingSolvesHardPuzzle() {
        int[][] solution = new BacktrackingSudokuSolver().solve(HARD_PUZZLE);
        assertIsValidSudokuSolution(solution);
        assertPreservesGivens(HARD_PUZZLE, solution);
    }

    @Test
    void dancingLinksSolvesHardPuzzle() {
        int[][] solution = new DancingLinksSudokuSolver().solve(HARD_PUZZLE);
        assertIsValidSudokuSolution(solution);
        assertPreservesGivens(HARD_PUZZLE, solution);
    }

    @Test
    void bothSolversAgreeOnSolution() {
        int[][] viaBacktracking = new BacktrackingSudokuSolver().solve(HARD_PUZZLE);
        int[][] viaDancingLinks = new DancingLinksSudokuSolver().solve(HARD_PUZZLE);
        assertArrayEquals(viaBacktracking, viaDancingLinks);
    }

    @Test
    void inputBoardIsNotMutated() {
        int[][] before = deepCopy(HARD_PUZZLE);
        new BacktrackingSudokuSolver().solve(HARD_PUZZLE);
        new DancingLinksSudokuSolver().solve(HARD_PUZZLE);
        assertArrayEquals(before, HARD_PUZZLE);
    }

    private static void assertIsValidSudokuSolution(int[][] board) {
        assertEquals(9, board.length);
        for (int row = 0; row < 9; row++) {
            assertEquals(9, board[row].length);
            boolean[] rowSeen = new boolean[10];
            for (int col = 0; col < 9; col++) {
                int v = board[row][col];
                assertTrue(v >= 1 && v <= 9, "value out of range at (" + row + "," + col + "): " + v);
                assertTrue(!rowSeen[v], "duplicate " + v + " in row " + row);
                rowSeen[v] = true;
            }
        }
        for (int col = 0; col < 9; col++) {
            boolean[] colSeen = new boolean[10];
            for (int row = 0; row < 9; row++) {
                int v = board[row][col];
                assertTrue(!colSeen[v], "duplicate " + v + " in column " + col);
                colSeen[v] = true;
            }
        }
        for (int boxRow = 0; boxRow < 9; boxRow += 3) {
            for (int boxCol = 0; boxCol < 9; boxCol += 3) {
                boolean[] boxSeen = new boolean[10];
                for (int r = boxRow; r < boxRow + 3; r++) {
                    for (int c = boxCol; c < boxCol + 3; c++) {
                        int v = board[r][c];
                        assertTrue(!boxSeen[v], "duplicate " + v + " in box at (" + boxRow + "," + boxCol + ")");
                        boxSeen[v] = true;
                    }
                }
            }
        }
    }

    private static void assertPreservesGivens(int[][] puzzle, int[][] solution) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (puzzle[row][col] != 0) {
                    assertEquals(puzzle[row][col], solution[row][col],
                            "given clue at (" + row + "," + col + ") was overwritten");
                }
            }
        }
        // sanity check: at least one cell was actually filled in by the solver
        int givenCount = 0;
        for (int[] row : puzzle) for (int v : row) if (v != 0) givenCount++;
        assertNotEquals(81, givenCount, "puzzle was already solved; nothing to verify");
    }

    private static int[][] deepCopy(int[][] board) {
        int[][] out = new int[board.length][];
        for (int i = 0; i < board.length; i++) out[i] = board[i].clone();
        return out;
    }
}
