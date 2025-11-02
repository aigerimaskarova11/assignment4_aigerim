package graph;

import graph.dagsp.DAGLongestPath;
import graph.dagsp.DAGShortestPath;
import graph.model.Graph;
import graph.model.GraphLoader;
import graph.scc.KosarajuSCC;
import graph.topo.KahnTopologicalSort;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Smart City/Campus Scheduling System");
        System.out.println("Assignment 4: Graph Algorithms");
        System.out.println("========================================\n");

        String[] categories = {"small", "medium", "large"};

        for (String category : categories) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("Processing " + category.toUpperCase() + " datasets");
            System.out.println("=".repeat(60));

            File folder = new File("data/" + category);
            if (!folder.exists()) {
                System.out.println("Warning: Directory data/" + category + " not found!");
                continue;
            }

            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                Arrays.sort(files);
                for (File file : files) {
                    processDataset(file.getPath());
                }
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("All datasets processed successfully!");
        System.out.println("=".repeat(60));
    }

    private static void processDataset(String filePath) {
        System.out.println("\n--- Processing: " + filePath + " ---");

        try {
            GraphLoader.GraphData data = GraphLoader.loadFromFile(filePath);
            Graph graph = data.graph;

            System.out.println("Graph loaded: " + graph.getNumVertices() +
                    " vertices, " + graph.getNumEdges() + " edges");

            // find strongly connected components
            System.out.println("\n1. Finding Strongly Connected Components...");
            KosarajuSCC sccFinder = new KosarajuSCC(graph);
            sccFinder.findSCCs();
            sccFinder.printSCCs();

            // build DAG of SCCs
            System.out.println("\n2. Building Condensation DAG...");
            Graph condensation = sccFinder.buildCondensationGraph();
            System.out.println("Condensation graph: " + condensation.getNumVertices() +
                    " components, " + condensation.getNumEdges() + " edges");

            // topological sort on DAG
            System.out.println("\n3. Topological Sort of Condensation...");
            KahnTopologicalSort topoSort = new KahnTopologicalSort(condensation);
            List<Integer> topoOrder = topoSort.sort();
            topoSort.printOrder();

            if (topoSort.isDAG()) {
                System.out.println("\n4. DAG Path Analysis...");
                DAGShortestPath shortestPath = new DAGShortestPath(condensation);
                shortestPath.computeShortestPaths(0);
                shortestPath.printPaths();

                DAGLongestPath longestPath = new DAGLongestPath(condensation);
                longestPath.computeLongestPaths(0);
                longestPath.printPaths();
            } else {
                System.out.println("\n4. Skipping path analysis (graph contains cycles)");
            }

            if (topoOrder != null) {
                System.out.println("\n5. Original Task Execution Order:");
                List<List<Integer>> sccs = sccFinder.getSCCs();

                System.out.print("Task order: ");
                for (int compId : topoOrder) {
                    System.out.print(sccs.get(compId) + " ");
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("Error processing " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
