# Assignment 4 – Smart City / Smart Campus Scheduling

## Introduction

This assignment focuses on planning and scheduling tasks in a smart city or smart campus. The goal is to use graph algorithms to handle task dependencies. Some tasks have cycles, and some form acyclic graphs. I implemented algorithms to detect cycles, sort tasks topologically, and find shortest and longest paths in a DAG. I also generated datasets to test my solutions on small, medium, and large graphs.

## 1. Data Summary

I created 9 datasets in total, divided into 3 categories:

| Category | Nodes (n) | Weight Model | Cyclic / DAG            |
| -------- | --------- | ------------ | ----------------------- |
| Small    | 6–10      | Edge         | Some cyclic, some DAG   |
| Medium   | 10–20     | Edge         | Mixed, several SCCs     |
| Large    | 20–50     | Edge         | Mixed, performance test |

All datasets are stored in `/data/`. Each dataset contains a directed graph with edges and a source node. Edge weights represent task duration.

## 2. Results (per-task)

| Graph Size | Task                    | Operations / Metric | Time (ms) |
| ---------- | ----------------------- | ------------------- | --------- |
| Small      | SCC (Kosaraju)          | ~20 DFS/edges       | <1        |
| Small      | Topological Sort (Kahn) | ~10 pops/pushes     | <1        |
| Small      | Shortest Path           | 5–10 relaxations    | <1        |
| Small      | Longest Path            | 5–10 relaxations    | <1        |
| Medium     | SCC                     | ~100 DFS/edges      | 1–2       |
| Medium     | Topological Sort        | ~50 pops/pushes     | <1        |
| Medium     | Shortest Path           | 30–50 relaxations   | <1        |
| Medium     | Longest Path            | 30–50 relaxations   | <1        |
| Large      | SCC                     | ~400 DFS/edges      | 3–5       |
| Large      | Topological Sort        | ~250 pops/pushes    | 2         |
| Large      | Shortest Path           | 150–200 relaxations | 2–3       |
| Large      | Longest Path            | 150–200 relaxations | 2–3       |

## 3. Analysis

* **SCC (Kosaraju)**: Detects cycles correctly. Time depends on number of edges. Large dense graphs take more DFS operations.
* **Condensation Graph + Topological Sort**: Works efficiently after SCC compression. Kahn’s algorithm is simple and fast.
* **Shortest/Longest Paths (DAG-SP)**: DP over topological order is efficient. Bottlenecks appear when the graph has many edges.
* **Effect of structure**: Dense graphs and large SCCs increase computation time. Sparse graphs are processed quickly. SCC size affects topological sort time slightly.

## 4. Conclusions

* **SCC** is needed for cyclic task dependencies.
* **Topological Sort** is needed to schedule tasks in DAGs after SCC compression.
* **Shortest Path** is useful to find minimal task duration or cost.
* **Longest Path** identifies the critical path for task planning.
* **Practical Recommendations**:

  * Use Kosaraju for cycle detection.
  * Use Kahn’s topological sort for acyclic planning.
  * Use DAG shortest/longest path for optimization of schedule.
  * Test on both dense and sparse graphs to check performance.
