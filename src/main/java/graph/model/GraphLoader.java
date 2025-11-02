package graph.model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GraphLoader {

    // load graph from JSON file
    public static GraphData loadFromFile(String filePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject json = new JSONObject(content);

        boolean directed = json.getBoolean("directed");
        int n = json.getInt("n");
        Graph graph = new Graph(n, directed);

        JSONArray edges = json.getJSONArray("edges");
        for (int i = 0; i < edges.length(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            int u = edge.getInt("u");
            int v = edge.getInt("v");
            int w = edge.optInt("w", 1);
            graph.addEdge(u, v, w);
        }

        int source = json.optInt("source", 0);
        String weightModel = json.optString("weight_model", "edge");

        return new GraphData(graph, source, weightModel);
    }

    // container for loaded graph data
    public static class GraphData {
        public final Graph graph;
        public final int source;
        public final String weightModel;

        public GraphData(Graph graph, int source, String weightModel) {
            this.graph = graph;
            this.source = source;
            this.weightModel = weightModel;
        }
    }
}
