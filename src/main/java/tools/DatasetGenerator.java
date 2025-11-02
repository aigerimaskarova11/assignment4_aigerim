package tools;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DatasetGenerator {
    private static final Random random = new Random(42); // fixed seed for reproducibility

    public static void main(String[] args) {
        try {
            Files.createDirectories(Paths.get("data/small"));
            Files.createDirectories(Paths.get("data/medium"));
            Files.createDirectories(Paths.get("data/large"));

            // small datasets
            generateDataset("data/small/small_dag.json", 8, 10, false, "sparse");
            generateDataset("data/small/small_cycle.json", 7, 12, true, "medium");
            generateDataset("data/small/small_multi_scc.json", 10, 18, true, "dense");

            // medium datasets
            generateDataset("data/medium/medium_dag.json", 15, 25, false, "sparse");
            generateDataset("data/medium/medium_mixed.json", 18, 40, true, "medium");
            generateDataset("data/medium/medium_multi_scc.json", 20, 60, true, "dense");

            // large datasets
            generateDataset("data/large/large_dag.json", 30, 60, false, "sparse");
            generateDataset("data/large/large_mixed.json", 40, 120, true, "medium");
            generateDataset("data/large/large_multi_scc.json", 50, 200, true, "dense");

            System.out.println("Successfully generated 9 datasets!");
            printDatasetSummary();

        } catch (IOException e) {
            System.err.println("Error creating datasets: " + e.getMessage());
        }
    }

    private static void generateDataset(String filePath, int numNodes, int numEdges,
                                        boolean allowCycles, String density) throws IOException {
        JSONObject json = new JSONObject();
        json.put("directed", true);
        json.put("n", numNodes);
        json.put("weight_model", "edge");
        json.put("source", 0);

        JSONArray edges;
        Set<String> addedEdges = new HashSet<>();

        if (allowCycles) {
            edges = generateGraphWithSCCs(numNodes, numEdges, addedEdges);
        } else {
            edges = generateDAG(numNodes, numEdges, addedEdges);
        }

        json.put("edges", edges);

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(json.toString(2)); // pretty print
            System.out.println("Generated: " + filePath);
        }
    }

    private static JSONArray generateDAG(int numNodes, int numEdges, Set<String> addedEdges) {
        JSONArray edges = new JSONArray();
        int edgeCount = 0;

        while (edgeCount < numEdges) {
            int u = random.nextInt(numNodes);
            int v = random.nextInt(numNodes);

            if (u < v) { // ensure DAG
                String edgeKey = u + "-" + v;
                if (!addedEdges.contains(edgeKey)) {
                    JSONObject edge = new JSONObject();
                    edge.put("u", u);
                    edge.put("v", v);
                    edge.put("w", random.nextInt(10) + 1);
                    edges.put(edge);
                    addedEdges.add(edgeKey);
                    edgeCount++;
                }
            }
        }
        return edges;
    }

    private static JSONArray generateGraphWithSCCs(int numNodes, int numEdges,
                                                   Set<String> addedEdges) {
        JSONArray edges = new JSONArray();

        int numSCCs = Math.min(numNodes / 3, 4);
        numSCCs = Math.max(numSCCs, 2);

        List<List<Integer>> sccs = new ArrayList<>();
        int nodesPerSCC = numNodes / numSCCs;

        for (int i = 0; i < numSCCs; i++) {
            List<Integer> scc = new ArrayList<>();
            int start = i * nodesPerSCC;
            int end = (i == numSCCs - 1) ? numNodes : (i + 1) * nodesPerSCC;
            for (int j = start; j < end; j++) scc.add(j);
            sccs.add(scc);
        }

        int edgeCount = 0;

        // basic cycle inside each SCC
        for (List<Integer> scc : sccs) {
            if (scc.size() > 1) {
                for (int i = 0; i < scc.size(); i++) {
                    int u = scc.get(i);
                    int v = scc.get((i + 1) % scc.size());
                    String edgeKey = u + "-" + v;
                    if (!addedEdges.contains(edgeKey)) {
                        JSONObject edge = new JSONObject();
                        edge.put("u", u);
                        edge.put("v", v);
                        edge.put("w", random.nextInt(10) + 1);
                        edges.put(edge);
                        addedEdges.add(edgeKey);
                        edgeCount++;
                    }
                }
            }
        }

        // add more random edges
        while (edgeCount < numEdges) {
            int u = random.nextInt(numNodes);
            int v = random.nextInt(numNodes);
            if (u != v) {
                String edgeKey = u + "-" + v;
                if (!addedEdges.contains(edgeKey)) {
                    JSONObject edge = new JSONObject();
                    edge.put("u", u);
                    edge.put("v", v);
                    edge.put("w", random.nextInt(10) + 1);
                    edges.put(edge);
                    addedEdges.add(edgeKey);
                    edgeCount++;
                }
            }
        }
        return edges;
    }

    private static void printDatasetSummary() {
        System.out.println("\n=== Dataset Summary ===");
        System.out.println("Category | File                        | Nodes | Type");
        System.out.println("---------|-----------------------------| ------|-------------");
        System.out.println("Small    | small_dag.json              | 8     | DAG (sparse)");
        System.out.println("Small    | small_cycle.json            | 7     | Cyclic");
        System.out.println("Small    | small_multi_scc.json        | 10    | Multi-SCC");
        System.out.println("Medium   | medium_dag.json             | 15    | DAG (sparse)");
        System.out.println("Medium   | medium_mixed.json           | 18    | Cyclic");
        System.out.println("Medium   | medium_multi_scc.json       | 20    | Multi-SCC");
        System.out.println("Large    | large_dag.json              | 30    | DAG (sparse)");
        System.out.println("Large    | large_mixed.json            | 40    | Cyclic");
        System.out.println("Large    | large_multi_scc.json        | 50    | Multi-SCC");
    }
}
