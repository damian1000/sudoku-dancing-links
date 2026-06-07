package io.github.damian1000.sudoku;

import java.util.stream.IntStream;

/**
 * Naive backtracking Sudoku solver. For each empty cell, tries 1-9 in order
 * and recurses; backtracks on first inconsistency. O(9^k) worst case where k
 * is the number of empty cells, but pruned by per-row / per-column / per-box
 * validation at every placement.
 */
public class BacktrackingSudokuSolver {

    private static final int SIZE = 9;

    public int[][] solve(int[][] input) {
        int[][] board = copy(input);
        if (!solveInPlace(board)) {
            throw new IllegalArgumentException("No solution exists for the given board");
        }
        return board;
    }

    private boolean solveInPlace(int[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (board[row][column] == 0) {
                    for (int k = 1; k <= SIZE; k++) {
                        board[row][column] = k;
                        if (isValid(board, row, column) && solveInPlace(board)) {
                            return true;
                        }
                        board[row][column] = 0;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int[][] board, int row, int column) {
        return rowConstraint(board, row)
                && columnConstraint(board, column)
                && subsectionConstraint(board, row, column);
    }

    private boolean rowConstraint(int[][] board, int row) {
        boolean[] constraint = new boolean[SIZE];
        return IntStream.range(0, SIZE)
                .allMatch(column -> checkConstraint(board, row, constraint, column));
    }

    private boolean columnConstraint(int[][] board, int column) {
        boolean[] constraint = new boolean[SIZE];
        return IntStream.range(0, SIZE)
                .allMatch(row -> checkConstraint(board, row, constraint, column));
    }

    private boolean subsectionConstraint(int[][] board, int row, int column) {
        boolean[] constraint = new boolean[SIZE];
        int subsectionRowStart = (row / 3) * 3;
        int subsectionColumnStart = (column / 3) * 3;
        for (int r = subsectionRowStart; r < subsectionRowStart + 3; r++) {
            for (int c = subsectionColumnStart; c < subsectionColumnStart + 3; c++) {
                if (!checkConstraint(board, r, constraint, c)) return false;
            }
        }
        return true;
    }

    private boolean checkConstraint(int[][] board, int row, boolean[] constraint, int column) {
        if (board[row][column] != 0) {
            if (!constraint[board[row][column] - 1]) {
                constraint[board[row][column] - 1] = true;
            } else {
                return false;
            }
        }
        return true;
    }

    private static int[][] copy(int[][] board) {
        int[][] out = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            out[i] = board[i].clone();
        }
        return out;
    }
}
