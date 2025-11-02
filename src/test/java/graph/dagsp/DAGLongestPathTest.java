package graph.dagsp;

import graph.model.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DAGLongestPathTest {

    @Test
    public void testSimplePath() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 2, 2);

        DAGLongestPath lp = new DAGLongestPath(graph);
        assertTrue(lp.computeLongestPaths(0));

        assertEquals(0, lp.getDistance(0));
        assertEquals(3, lp.getDistance(1));
        assertEquals(5, lp.getDistance(2));
    }

    @Test
    public void testMultiplePaths() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 4);

        DAGLongestPath lp = new DAGLongestPath(graph);
        assertTrue(lp.computeLongestPaths(0));

        // longest path to node 3 should be 0 -> 2 -> 3 = 7
        assertEquals(7, lp.getDistance(3));

        List<Integer> path = lp.getPath(3);
        assertNotNull(path);
    }

    @Test
    public void testCriticalPath() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 4);

        DAGLongestPath lp = new DAGLongestPath(graph);
        lp.computeLongestPaths(0);

        DAGLongestPath.CriticalPathResult critical = lp.findCriticalPath();
        assertNotNull(critical);
        assertEquals(7, critical.length);
        assertEquals(3, critical.path.size());
    }

    @Test
    public void testUnreachableVertex() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 5);

        DAGLongestPath lp = new DAGLongestPath(graph);
        assertTrue(lp.computeLongestPaths(0));

        // vertex 2 is not reachable
        assertEquals(Integer.MIN_VALUE, lp.getDistance(2));
    }

    @Test
    public void testCyclicGraph() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        DAGLongestPath lp = new DAGLongestPath(graph);
        // should fail because graph has a cycle
        assertFalse(lp.computeLongestPaths(0));
    }
}
