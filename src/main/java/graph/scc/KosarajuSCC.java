package graph.scc;

import graph.model.Edge;
import graph.model.Graph;
import util.Metrics;
import java.util.*;

public class KosarajuSCC {
    private final Graph graph;
    private final Metrics metrics;
    private List<List<Integer>> sccs;
    private int[] componentId;
    private int numComponents;

    public KosarajuSCC(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics("Kosaraju SCC");
    }

    // find all strongly connected components
    public void findSCCs() {
        metrics.startTimer();

        int n = graph.getNumVertices();
        sccs = new ArrayList<>();
        componentId = new int[n];
        Arrays.fill(componentId, -1);

        boolean[] visited = new boolean[n];
        Stack<Integer> finishStack = new Stack<>();

        // first DFS to record finish order
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs1(i, visited, finishStack);
            }
        }

        Graph reverseGraph = graph.getReverse();
        visited = new boolean[n];
        numComponents = 0;

        // second DFS on reversed graph
        while (!finishStack.isEmpty()) {
            int v = finishStack.pop();
            if (!visited[v]) {
                List<Integer> component = new ArrayList<>();
                dfs2(v, visited, component, reverseGraph);

                for (int vertex : component) {
                    componentId[vertex] = numComponents;
                }

                sccs.add(component);
                numComponents++;
            }
        }

        metrics.stopTimer();
    }

    private void dfs1(int v, boolean[] visited, Stack<Integer> finishStack) {
        visited[v] = true;
        metrics.incrementOperations();
        for (Edge e : graph.getEdges(v)) {
            metrics.incrementOperations();
            if (!visited[e.getTo()]) {
                dfs1(e.getTo(), visited, finishStack);
            }
        }
        finishStack.push(v);
    }

    private void dfs2(int v, boolean[] visited, List<Integer> component, Graph g) {
        visited[v] = true;
        component.add(v);
        metrics.incrementOperations();
        for (Edge e : g.getEdges(v)) {
            metrics.incrementOperations();
            if (!visited[e.getTo()]) {
                dfs2(e.getTo(), visited, component, g);
            }
        }
    }

    // build condensation graph
    public Graph buildCondensationGraph() {
        if (sccs == null) {
            throw new IllegalStateException("Must call findSCCs() first");
        }

        Graph condensation = new Graph(numComponents, true);
        Set<String> addedEdges = new HashSet<>();

        for (int u = 0; u < graph.getNumVertices(); u++) {
            int compU = componentId[u];
            for (Edge e : graph.getEdges(u)) {
                int v = e.getTo();
                int compV = componentId[v];
                if (compU != compV) {
                    String edgeKey = compU + "-" + compV;
                    if (!addedEdges.contains(edgeKey)) {
                        condensation.addEdge(compU, compV, e.getWeight());
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }

        return condensation;
    }

    public List<List<Integer>> getSCCs() {
        return sccs;
    }

    public int[] getComponentIds() {
        return componentId;
    }

    public int getNumComponents() {
        return numComponents;
    }

    public void printSCCs() {
        System.out.println("=== Strongly Connected Components ===");
        System.out.println("Total components: " + numComponents);
        for (int i = 0; i < sccs.size(); i++) {
            List<Integer> scc = sccs.get(i);
            System.out.printf("SCC %d (size %d): %s\n", i, scc.size(), scc);
        }
        System.out.println(metrics);
    }
}
