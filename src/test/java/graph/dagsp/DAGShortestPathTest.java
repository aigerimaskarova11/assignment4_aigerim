package graph.dagsp;

import graph.model.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DAGShortestPathTest {

    @Test
    public void testSimplePath() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 2, 2);

        DAGShortestPath sp = new DAGShortestPath(graph);
        assertTrue(sp.computeShortestPaths(0));

        assertEquals(0, sp.getDistance(0));
        assertEquals(3, sp.getDistance(1));
        assertEquals(5, sp.getDistance(2));
    }

    @Test
    public void testMultiplePaths() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 4);

        DAGShortestPath sp = new DAGShortestPath(graph);
        assertTrue(sp.computeShortestPaths(0));

        assertEquals(0, sp.getDistance(0));
        assertEquals(5, sp.getDistance(1));
        assertEquals(3, sp.getDistance(2));
        assertEquals(6, sp.getDistance(3));

        // shortest path to 3 should exist and have 3 nodes
        List<Integer> path = sp.getPath(3);
        assertNotNull(path);
        assertEquals(3, path.size());
    }

    @Test
    public void testUnreachableVertex() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 5);

        DAGShortestPath sp = new DAGShortestPath(graph);
        assertTrue(sp.computeShortestPaths(0));

        // vertex 2 is not reachable
        assertEquals(Integer.MAX_VALUE, sp.getDistance(2));
        assertNull(sp.getPath(2));
    }

    @Test
    public void testCyclicGraph() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        DAGShortestPath sp = new DAGShortestPath(graph);
        // should return false for a cyclic graph
        assertFalse(sp.computeShortestPaths(0));
    }
}
