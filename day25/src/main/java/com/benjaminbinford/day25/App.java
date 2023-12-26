package com.benjaminbinford.day25;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.typemeta.funcj.tuples.Tuple2;

import com.benjaminbinford.utils.AdventException;
import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    static record Partition(Graph g1, Graph g2, List<Tuple2<String, String>> edges) {

        /**
         * Partition this graph into 2 unconnected subgraphs using
         * https://en.wikipedia.org/wiki/Karger%27s_algorithm
         * 
         */
        public static Partition partition(Graph input) {
            return partition(input, 0);
        }

        public static Partition partition(Graph input, int count) {

            if (count > 100) {
                throw new AdventException("Too many iterations");
            }
            var vertexRenaming = new VertexRenaming();

            var g = new Graph(input);
            while (g.size() > 2 && !g.edges.isEmpty()) {
                var edgeIndex = r.nextInt(g.edges.size());
                var edge = g.edges.get(edgeIndex);
                g.edges.remove(edgeIndex);

                String mappedEdge1 = vertexRenaming.get(edge.get1());
                String mappedEdge2 = vertexRenaming.get(edge.get2());

                if (!g.vertices.remove(mappedEdge1)) {
                    throw new AdventException(
                            "Vertex not found " + edge.get1());
                }
                if (!mappedEdge1.equals(mappedEdge2) && !g.vertices.remove(mappedEdge2)) {
                    throw new AdventException(
                            "Vertex not found " + edge.get2());
                }

                var mergedId = vertexRenaming.allocate();
                g.vertices.add(mergedId);
                vertexRenaming.rename(mappedEdge1, mergedId);
                vertexRenaming.rename(mappedEdge2, mergedId);

                // // Replace all references to the merged vertices with the merged id
                // for (var i = 0; i < g.edges.size(); i++) {
                // var e = g.edges.get(i);
                // if (e.get1().equals(edge.get1()) || e.get1().equals(edge.get2())) {
                // g.edges.set(i, Tuple2.of(mergedId, e.get2()));
                // }
                // e = g.edges.get(i);
                // if (e.get2().equals(edge.get1()) || e.get2().equals(edge.get2())) {
                // g.edges.set(i, Tuple2.of(e.get1(), mergedId));
                // }
                // }
            }

            List<Tuple2<String, String>> cutlist = g.edges.stream()
                    .filter(e -> !vertexRenaming.get(e.get1()).equals(vertexRenaming.get(e.get2())))
                    .toList();

            if (cutlist.size() != 3) {
                return partition(input, count + 1);
            } else {
                return new Partition(vertexRenaming.generateGraph(g.vertices.get(0), g.edges),
                        vertexRenaming.generateGraph(g.vertices.get(1), g.edges),
                        cutlist);
            }
        }
    }

    static class VertexRenaming {
        int counter = 1;
        Map<String, String> renames = new HashMap<>();

        public String get(String vertex) {

            while (true) {
                var nextVertex = renames.get(vertex);
                if (nextVertex == null) {
                    return vertex;
                } else {
                    vertex = nextVertex;
                }
            }

        }

        public void rename(String vertex, String newVertex) {
            renames.put(vertex, newVertex);
        }

        public String allocate() {
            return "*" + counter++;
        }

        public Graph generateGraph(String vertex, List<Tuple2<String, String>> edges) {
            var g = new Graph();
            materializeVertices(vertex, g);
            edges.forEach(e -> {
                if (get(e.get1()).equals(vertex) && get(e.get2()).equals(vertex)) {
                    g.addEdge(e.get1(), e.get2());
                }
            });
            return g;
        }

        private void materializeVertices(String vertex, Graph g) {
            if (vertex.startsWith("*")) {
                for (var e : renames.entrySet()) {
                    if (e.getValue().equals(vertex)) {
                        materializeVertices(e.getKey(), g);
                    }
                }
            } else {
                g.addVertex(vertex);
            }
        }
    }

    static Random r = new Random();

    static class Graph {
        List<String> vertices;
        List<Tuple2<String, String>> edges;

        public void addVertex(String vertex) {
            if (!vertices.contains(vertex)) {
                vertices.add(vertex);
            }
        }

        public void addEdge(String v1, String v2) {
            addVertex(v1);
            addVertex(v2);

            edges.add(Tuple2.of(v1, v2));
        }

        public Graph() {
            vertices = new ArrayList<>();
            edges = new ArrayList<>();
        }

        public Graph(String input) {
            this();
            input.lines().forEach(rep -> {
                var parts = rep.split("[ :]+");

                for (var i = 1; i < parts.length; i++) {

                    addEdge(parts[0], parts[i]);

                }
            });
        }

        public Graph(Graph input) {
            // Copy constructor
            this.vertices = new ArrayList<>(input.vertices);
            this.edges = new ArrayList<>(input.edges);
        }

        public int size() {
            return vertices.size();
        }

        public List<String> getVertices() {
            return vertices;
        }
    }

    Graph g;

    public App(String input) {

        g = new Graph(input);

    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day25/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);
        var ps = App.Partition.partition(app.g);
        IO.answer(ps.g1().size() * ps.g2().size());
        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }

}
