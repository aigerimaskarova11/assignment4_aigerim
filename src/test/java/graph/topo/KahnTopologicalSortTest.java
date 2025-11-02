package graph.topo;

import graph.model.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class KahnTopologicalSortTest {

    @Test
    public void testSimpleDAG() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);

        KahnTopologicalSort topoSort = new KahnTopologicalSort(graph);
        List<Integer> order = topoSort.sort();

        assertNotNull(order);
        assertEquals(3, order.size());
        assertTrue(topoSort.isDAG());

        // check basic order: 0 -> 1 -> 2
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(1) < order.indexOf(2));
    }

    @Test
    public void testDiamondDAG() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);

        KahnTopologicalSort topoSort = new KahnTopologicalSort(graph);
        List<Integer> order = topoSort.sort();

        assertNotNull(order);
        assertEquals(4, order.size());
        assertTrue(topoSort.isDAG());

        // 0 should be first, 3 last
        assertEquals(0, order.get(0).intValue());
        assertEquals(3, order.get(3).intValue());
    }

    @Test
    public void testCyclicGraph() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);

        KahnTopologicalSort topoSort = new KahnTopologicalSort(graph);
        List<Integer> order = topoSort.sort();

        assertNull(order);
        assertFalse(topoSort.isDAG());
    }

    @Test
    public void testDisconnectedDAG() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1);
        graph.addEdge(2, 3);

        KahnTopologicalSort topoSort = new KahnTopologicalSort(graph);
        List<Integer> order = topoSort.sort();

        assertNotNull(order);
        assertEquals(4, order.size());
        assertTrue(topoSort.isDAG());
    }

    @Test
    public void testSingleVertex() {
        Graph graph = new Graph(1, true);

        KahnTopologicalSort topoSort = new KahnTopologicalSort(graph);
        List<Integer> order = topoSort.sort();

        assertNotNull(order);
        assertEquals(1, order.size());
        assertEquals(0, order.get(0).intValue());
    }

    @Test
    public void testComplexDAG() {
        Graph graph = new Graph(6, true);
        graph.addEdge(5, 2);
        graph.addEdge(5, 0);
        graph.addEdge(4, 0);
        graph.addEdge(4, 1);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);

        KahnTopologicalSort topoSort = new KahnTopologicalSort(graph);
        List<Integer> order = topoSort.sort();

        assertNotNull(order);
        assertEquals(6, order.size());
        assertTrue(topoSort.isDAG());

        // verify key ordering constraints
        assertTrue(order.indexOf(5) < order.indexOf(2));
        assertTrue(order.indexOf(2) < order.indexOf(3));
        assertTrue(order.indexOf(3) < order.indexOf(1));
    }
}
