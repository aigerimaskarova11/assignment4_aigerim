package graph.dagsp;

import graph.model.Edge;
import graph.model.Graph;
import graph.topo.KahnTopologicalSort;
import util.Metrics;
import java.util.*;

public class DAGShortestPath {
    private final Graph graph;
    private final Metrics metrics;
    private int[] distances;
    private int[] predecessors;
    private int source;

    public DAGShortestPath(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics("DAG Shortest Path");
    }

    // compute the shortest paths from source vertex
    public boolean computeShortestPaths(int source) {
        this.source = source;
        metrics.startTimer();

        int n = graph.getNumVertices();

        KahnTopologicalSort topoSort = new KahnTopologicalSort(graph);
        List<Integer> topoOrder = topoSort.sort();

        if (topoOrder == null) {
            metrics.stopTimer();
            System.err.println("Error: Graph contains a cycle");
            return false;
        }

        distances = new int[n];
        predecessors = new int[n];
        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(predecessors, -1);
        distances[source] = 0;

        // relax edges following topo order
        for (int u : topoOrder) {
            if (distances[u] != Integer.MAX_VALUE) {
                for (Edge e : graph.getEdges(u)) {
                    int v = e.getTo();
                    int newDist = distances[u] + e.getWeight();
                    metrics.incrementOperations();
                    if (newDist < distances[v]) {
                        distances[v] = newDist;
                        predecessors[v] = u;
                    }
                }
            }
        }

        metrics.stopTimer();
        return true;
    }

    public int getDistance(int vertex) {
        if (distances == null)
            throw new IllegalStateException("Must call computeShortestPaths() first");
        return distances[vertex];
    }

    // reconstruct the shortest path
    public List<Integer> getPath(int destination) {
        if (predecessors == null)
            throw new IllegalStateException("Must call computeShortestPaths() first");

        if (distances[destination] == Integer.MAX_VALUE)
            return null;

        LinkedList<Integer> path = new LinkedList<>();
        int current = destination;
        while (current != -1) {
            path.addFirst(current);
            current = predecessors[current];
        }
        return path;
    }

    public void printPaths() {
        System.out.println("=== Shortest Paths from vertex " + source + " ===");
        for (int i = 0; i < graph.getNumVertices(); i++) {
            if (distances[i] == Integer.MAX_VALUE) {
                System.out.printf("Vertex %d: unreachable\n", i);
            } else {
                List<Integer> path = getPath(i);
                System.out.printf("Vertex %d: distance=%d, path=%s\n",
                        i, distances[i], path);
            }
        }
        System.out.println(metrics);
    }
}
