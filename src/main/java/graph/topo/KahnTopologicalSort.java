package graph.topo;

import graph.model.Edge;
import graph.model.Graph;
import util.Metrics;
import java.util.*;

public class KahnTopologicalSort {
    private final Graph graph;
    private final Metrics metrics;
    private List<Integer> topologicalOrder;

    public KahnTopologicalSort(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics("Kahn Topological Sort");
    }

    // compute topological order; return null if graph has cycle
    public List<Integer> sort() {
        metrics.startTimer();

        int n = graph.getNumVertices();
        int[] inDegree = new int[n];

        // calculate in-degrees
        for (int u = 0; u < n; u++) {
            for (Edge e : graph.getEdges(u)) {
                inDegree[e.getTo()]++;
                metrics.incrementOperations();
            }
        }

        // collect all with in-degree 0
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementOperations();
            }
        }

        topologicalOrder = new ArrayList<>();

        // process queue
        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementOperations();
            topologicalOrder.add(u);

            for (Edge e : graph.getEdges(u)) {
                int v = e.getTo();
                inDegree[v]--;
                metrics.incrementOperations();
                if (inDegree[v] == 0) {
                    queue.offer(v);
                    metrics.incrementOperations();
                }
            }
        }

        metrics.stopTimer();

        if (topologicalOrder.size() != n) {
            System.err.println("Warning: Graph contains a cycle!");
            return null;
        }

        return topologicalOrder;
    }

    public boolean isDAG() {
        return topologicalOrder != null &&
                topologicalOrder.size() == graph.getNumVertices();
    }

    public void printOrder() {
        System.out.println("=== Topological Order ===");
        if (topologicalOrder == null) {
            System.out.println("Graph contains a cycle - no topological order exists");
        } else {
            System.out.println("Order: " + topologicalOrder);
        }
        System.out.println(metrics);
    }
}
