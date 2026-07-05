package io.github.damian1000.sudoku.dancing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Knuth's Dancing Links (Algorithm X) for exact cover problems.
 * <p>
 * The matrix is represented as a doubly-linked toroidal grid of {@link DancingNode}s
 * with {@link ColumnNode} headers. {@code cover()} unlinks a column and all rows
 * crossing it in O(rows × non-zeros) work, then {@code uncover()} relinks them
 * by reattaching the unchanged pointers — that's where the name comes from.
 * <p>
 * Column selection uses the S-heuristic: pick the column with the fewest
 * remaining 1s. This prunes the search tree aggressively because we recurse
 * into the most constrained subproblem first.
 * <p>
 * This class knows nothing about Sudoku: it consumes a boolean cover matrix and
 * reports the chosen rows as column-index sets. Interpreting those rows back
 * into a domain (a Sudoku grid, a pentomino tiling, ...) is the caller's job.
 */
public class DancingLinks {

    private final ColumnNode header;
    private List<DancingNode> answer;
    private List<List<Integer>> answerRows;

    DancingLinks(boolean[][] cover) {
        header = makeDLXBoard(cover);
    }

    public void runSolver() {
        answer = new ArrayList<>();
        answerRows = null;
        search(0);
    }

    /**
     * The exact cover found by {@link #runSolver()}, or {@code null} if none exists:
     * one entry per selected row, each listing that row's column indices in ascending order.
     */
    public List<List<Integer>> getAnswerRows() {
        return answerRows;
    }

    private void search(int k) {
        if (header.R == header) {
            answerRows = toAnswerRows(answer);
            return;
        }
        ColumnNode c = selectColumnNodeHeuristic();
        c.cover();

        for (DancingNode r = c.D; r != c; r = r.D) {
            answer.add(r);

            for (DancingNode j = r.R; j != r; j = j.R) {
                j.C.cover();
            }

            search(k + 1);
            if (answerRows != null) return;

            r = answer.remove(answer.size() - 1);
            c = r.C;

            for (DancingNode j = r.L; j != r; j = j.L) {
                j.C.uncover();
            }
        }
        c.uncover();
    }

    private ColumnNode selectColumnNodeHeuristic() {
        int min = Integer.MAX_VALUE;
        ColumnNode ret = null;
        for (ColumnNode c = (ColumnNode) header.R; c != header; c = (ColumnNode) c.R) {
            if (c.size < min) {
                min = c.size;
                ret = c;
            }
        }
        return ret;
    }

    private ColumnNode makeDLXBoard(boolean[][] grid) {
        final int COLS = grid[0].length;

        ColumnNode headerNode = new ColumnNode("header");
        List<ColumnNode> columnNodes = new ArrayList<>();

        for (int i = 0; i < COLS; i++) {
            ColumnNode n = new ColumnNode(Integer.toString(i));
            columnNodes.add(n);
            headerNode = (ColumnNode) headerNode.hookRight(n);
        }
        headerNode = headerNode.R.C;

        for (boolean[] aGrid : grid) {
            DancingNode prev = null;
            for (int j = 0; j < COLS; j++) {
                if (aGrid[j]) {
                    ColumnNode col = columnNodes.get(j);
                    DancingNode newNode = new DancingNode(col);
                    if (prev == null) prev = newNode;
                    col.U.hookDown(newNode);
                    prev = prev.hookRight(newNode);
                    col.size++;
                }
            }
        }

        headerNode.size = COLS;
        return headerNode;
    }

    private static List<List<Integer>> toAnswerRows(List<DancingNode> answer) {
        List<List<Integer>> rows = new ArrayList<>();
        for (DancingNode n : answer) {
            List<Integer> columns = new ArrayList<>();
            columns.add(Integer.parseInt(n.C.name));
            for (DancingNode tmp = n.R; tmp != n; tmp = tmp.R) {
                columns.add(Integer.parseInt(tmp.C.name));
            }
            Collections.sort(columns);
            rows.add(columns);
        }
        return rows;
    }
}
