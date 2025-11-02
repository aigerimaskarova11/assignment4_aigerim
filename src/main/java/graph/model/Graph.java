package graph.model;

import java.util.*;

public class Graph {
    private final int numVertices;
    private final List<List<Edge>> adjList;
    private final boolean directed;

    public Graph(int numVertices, boolean directed) {
        this.numVertices = numVertices;
        this.directed = directed;
        this.adjList = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    // add edge with weight
    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= numVertices || to < 0 || to >= numVertices) {
            throw new IllegalArgumentException("Invalid vertex index");
        }
        adjList.get(from).add(new Edge(from, to, weight));
    }

    // add edge with default weight = 1
    public void addEdge(int from, int to) {
        addEdge(from, to, 1);
    }

    // return edges from vertex
    public List<Edge> getEdges(int vertex) {
        if (vertex < 0 || vertex >= numVertices) {
            throw new IllegalArgumentException("Invalid vertex index");
        }
        return adjList.get(vertex);
    }

    public int getNumVertices() {
        return numVertices;
    }

    public boolean isDirected() {
        return directed;
    }

    // build reversed graph
    public Graph getReverse() {
        Graph reverse = new Graph(numVertices, directed);
        for (int u = 0; u < numVertices; u++) {
            for (Edge e : adjList.get(u)) {
                reverse.addEdge(e.getTo(), e.getFrom(), e.getWeight());
            }
        }
        return reverse;
    }

    // total edge count
    public int getNumEdges() {
        int count = 0;
        for (List<Edge> edges : adjList) {
            count += edges.size();
        }
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Graph: %d vertices, %d edges\n",
                numVertices, getNumEdges()));
        for (int i = 0; i < numVertices; i++) {
            sb.append(String.format("  %d: ", i));
            for (Edge e : adjList.get(i)) {
                sb.append(String.format("->%d(w=%d) ", e.getTo(), e.getWeight()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
