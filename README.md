# Sudoku Dancing Links

[![CI](https://github.com/damian1000/sudoku-dancing-links/actions/workflows/ci.yml/badge.svg)](https://github.com/damian1000/sudoku-dancing-links/actions/workflows/ci.yml)
[![JDK](https://img.shields.io/badge/jdk-25-orange)](https://openjdk.org/projects/jdk/25/)

Two Sudoku solvers side by side: naive backtracking and Knuth's **Dancing Links** (Algorithm X). Same input, same output, very different work.

## Why two solvers

Naive backtracking is the obvious algorithm — try a digit, recurse, undo on failure. It works fine on easy puzzles and falls off a cliff on hard ones because it re-validates the same constraints over and over.

Dancing Links reframes Sudoku as an **exact cover** problem and solves it with O(1) row/column unlink/relink via doubly-linked toroidal lists. The "dancing" name comes from the trick that uncovering a node only needs the pointers *still inside* the node — the surrounding list elements never had their pointers updated, so they automatically reattach.

## Headline timing

On the same "world's hardest" puzzle (Knuth's classic, 17 clues), measured by the JUnit suite in this repo:

| Solver | Time |
|---|---:|
| `BacktrackingSudokuSolver` | ~140 ms |
| `DancingLinksSudokuSolver` | ~6 ms |

~20× faster, same answer. Larger constraint-rich problems amplify the gap further.

## Structure

```
sudoku/
├── BacktrackingSudokuSolver.java     # baseline: try-and-undo recursion with per-cell validation
└── dancing/
    ├── DancingNode.java              # 4-way linked node with hookRight / hookDown / unlinkLR / unlinkUD
    ├── ColumnNode.java               # header node with cover() / uncover() — the dancing
    ├── DancingLinks.java             # Algorithm X: recursive search with S-heuristic column selection
    └── DancingLinksSudokuSolver.java # maps Sudoku to a 729×324 exact cover matrix and runs DLX
```

## The exact cover encoding

Each of the 9×9×9 = 729 candidate placements (row, column, digit) is a row in the cover matrix. Each row has exactly four 1s, one per constraint family:

| Constraint family | Count | "Each \_\_ must contain exactly one \_\_" |
|---|---|---|
| Cell | 81 | each cell × one digit |
| Row | 81 | each row × each digit |
| Column | 81 | each row × each digit |
| Box | 81 | each 3×3 box × each digit |

Total: 729 rows × 324 columns. Algorithm X picks the column with fewest remaining 1s (the **S-heuristic**), tries each row covering that column, recurses, then uncovers on backtrack.

## Run

```bash
./gradlew test              # 4 tests; both solvers must agree on the hard puzzle
```

```java
int[][] puzzle = { /* ... 9×9 with 0 for empty ... */ };

int[][] solved = new DancingLinksSudokuSolver().solve(puzzle);
// or
int[][] solvedAlt = new BacktrackingSudokuSolver().solve(puzzle);
```

Both `solve(int[][])` methods are non-mutating — they return a new 9×9 array and leave the input untouched.

## Stack

- JDK 25 toolchain
- JUnit Jupiter 6.1
- Gradle 9.5.1

No third-party dependencies in the main source set. Pure JDK.

## Further reading

- Knuth, "Dancing Links" — [arXiv:cs/0011047](https://arxiv.org/abs/cs/0011047)
- The S-heuristic and uncover symmetry are both Knuth's, not folklore — worth reading the original paper.

## License

Apache 2.0 — see [LICENSE](LICENSE).
