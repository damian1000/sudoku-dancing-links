# TODO

## Roadmap (prioritized)

External review framing: "keep as an algorithms example". The repo is already well-scoped for that — coverage is ~99%, both solvers are tested against known boards including unsolvables. Don't try to grow it into something it isn't.

### P1 — nothing pressing

This one is fine. No correctness issues, no overclaim, README accurately describes what's there.

### P2 — only if you want to extend the algorithms story

Pick at most one; otherwise leave it alone:

- **Add a benchmarking section to the README** with concrete numbers (e.g. "easy board: 50 µs backtracking, 12 µs DLX; hard board: 8 s backtracking, 4 ms DLX"). JMH already in the family; would make the speed-up claim concrete.
- **Generalise DLX to other exact-cover problems** (N-queens, pentomino tiling). Shows the technique, not just the application. Each is ~100 lines and a separate `Solver` class.

### P3 — explicitly not worth doing

Don't add: parallel solving, GPU implementation, web UI. They'd dilute the focus without changing what this repo demonstrates.