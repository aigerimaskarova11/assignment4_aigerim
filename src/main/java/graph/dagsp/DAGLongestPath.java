package graph.dagsp;

import graph.model.Edge;
import graph.model.Graph;
import graph.topo.KahnTopologicalSort;
import util.Metrics;
import java.util.*;

public class DAGLongestPath {
    private final Graph graph;
    private final Metrics metrics;
    private int[] distances;
    private int[] predecessors;
    private int source;

    public DAGLongestPath(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics("DAG Longest Path");
    }

    // compute longest paths from source
    public boolean computeLongestPaths(int source) {
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
        Arrays.fill(distances, Integer.MIN_VALUE);
        Arrays.fill(predecessors, -1);
        distances[source] = 0;

        for (int u : topoOrder) {
            if (distances[u] != Integer.MIN_VALUE) {
                for (Edge e : graph.getEdges(u)) {
                    int v = e.getTo();
                    int newDist = distances[u] + e.getWeight();
                    metrics.incrementOperations();

                    if (newDist > distances[v]) {
                        distances[v] = newDist;
                        predecessors[v] = u;
                    }
                }
            }
        }

        metrics.stopTimer();
        return true;
    }

    // find critical (longest) path
    public CriticalPathResult findCriticalPath() {
        if (distances == null) {
            throw new IllegalStateException("Must call computeLongestPaths() first");
        }

        int maxVertex = -1;
        int maxDistance = Integer.MIN_VALUE;

        for (int i = 0; i < distances.length; i++) {
            if (distances[i] != Integer.MIN_VALUE && distances[i] > maxDistance) {
                maxDistance = distances[i];
                maxVertex = i;
            }
        }

        if (maxVertex == -1) {
            return null;
        }

        List<Integer> path = getPath(maxVertex);
        return new CriticalPathResult(path, maxDistance);
    }

    public int getDistance(int vertex) {
        if (distances == null) {
            throw new IllegalStateException("Must call computeLongestPaths() first");
        }
        return distances[vertex];
    }

    // reconstruct the longest path
    public List<Integer> getPath(int destination) {
        if (predecessors == null) {
            throw new IllegalStateException("Must call computeLongestPaths() first");
        }

        if (distances[destination] == Integer.MIN_VALUE) {
            return null;
        }

        LinkedList<Integer> path = new LinkedList<>();
        int current = destination;

        while (current != -1) {
            path.addFirst(current);
            current = predecessors[current];
        }

        return path;
    }

    public void printPaths() {
        System.out.println("=== Longest Paths from vertex " + source + " ===");

        for (int i = 0; i < graph.getNumVertices(); i++) {
            if (distances[i] == Integer.MIN_VALUE) {
                System.out.printf("Vertex %d: unreachable\n", i);
            } else {
                List<Integer> path = getPath(i);
                System.out.printf("Vertex %d: distance=%d, path=%s\n",
                        i, distances[i], path);
            }
        }

        CriticalPathResult critical = findCriticalPath();
        if (critical != null) {
            System.out.println("\nCritical Path: " + critical.path);
            System.out.println("Critical Path Length: " + critical.length);
        }

        System.out.println(metrics);
    }

    public static class CriticalPathResult {
        public final List<Integer> path;
        public final int length;

        public CriticalPathResult(List<Integer> path, int length) {
            this.path = path;
            this.length = length;
        }
    }
}
