package graph.scc;

import graph.model.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class KosarajuSCCTest {

    @Test
    public void testSimpleCycle() {
        // 0 -> 1 -> 2 -> 0 (one SCC)
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);

        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();

        assertEquals(1, scc.getNumComponents());
        assertEquals(3, scc.getSCCs().get(0).size());
    }

    @Test
    public void testTwoSCCs() {
        // 0 <-> 1, 2 <-> 3
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 0);
        graph.addEdge(2, 3);
        graph.addEdge(3, 2);

        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();

        assertEquals(2, scc.getNumComponents());
    }

    @Test
    public void testDAG() {
        // 0 -> 1 -> 2 (3 SCCs)
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);

        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();

        assertEquals(3, scc.getNumComponents());
        for (List<Integer> c : scc.getSCCs()) assertEquals(1, c.size());
    }

    @Test
    public void testComplexGraph() {
        // 1->2->3->1 forms cycle
        Graph graph = new Graph(8, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);
        graph.addEdge(4, 5);
        graph.addEdge(5, 6);
        graph.addEdge(6, 7);

        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();

        assertTrue(scc.getNumComponents() >= 2);
        int[] comp = scc.getComponentIds();
        assertEquals(comp[1], comp[2]);
        assertEquals(comp[2], comp[3]);
    }

    @Test
    public void testSingleVertex() {
        Graph graph = new Graph(1, true);

        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();

        assertEquals(1, scc.getNumComponents());
        assertEquals(1, scc.getSCCs().get(0).size());
    }

    @Test
    public void testDisconnectedGraph() {
        // two isolated vertices
        Graph graph = new Graph(2, true);

        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();

        assertEquals(2, scc.getNumComponents());
    }

    @Test
    public void testCondensationDAG() {
        // 0->1->2->0 forms one SCC, 3->4 another part
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(0, 3);
        graph.addEdge(3, 4);

        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        Graph condensation = scc.buildCondensationGraph();

        assertNotNull(condensation);
        assertTrue(condensation.getNumVertices() >= 2);
        assertTrue(condensation.getNumEdges() >= 1);
    }
}
