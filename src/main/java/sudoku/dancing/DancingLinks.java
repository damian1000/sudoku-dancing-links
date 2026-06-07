package sudoku.dancing;

import java.util.ArrayList;
import java.util.LinkedList;
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
 */
public class DancingLinks {

    private static final int SUDOKU_SIZE = 9;

    private final ColumnNode header;
    private List<DancingNode> answer;
    private int[][] solution;

    DancingLinks(boolean[][] cover) {
        header = makeDLXBoard(cover);
    }

    public void runSolver() {
        answer = new LinkedList<>();
        solution = null;
        search(0);
    }

    public int[][] getSolution() {
        return solution;
    }

    private void search(int k) {
        if (header.R == header) {
            solution = parseBoard(answer);
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
            if (solution != null) return;

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

    private int[][] parseBoard(List<DancingNode> answer) {
        int[][] result = new int[SUDOKU_SIZE][SUDOKU_SIZE];
        for (DancingNode n : answer) {
            DancingNode rcNode = n;
            int min = Integer.parseInt(rcNode.C.name);
            for (DancingNode tmp = n.R; tmp != n; tmp = tmp.R) {
                int val = Integer.parseInt(tmp.C.name);
                if (val < min) {
                    min = val;
                    rcNode = tmp;
                }
            }
            int ans1 = Integer.parseInt(rcNode.C.name);
            int ans2 = Integer.parseInt(rcNode.R.C.name);
            int r = ans1 / SUDOKU_SIZE;
            int c = ans1 % SUDOKU_SIZE;
            int num = (ans2 % SUDOKU_SIZE) + 1;
            result[r][c] = num;
        }
        return result;
    }
}
