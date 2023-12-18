package com.benjaminbinford.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Predicate;

public class Dijkstra<V, E extends Weight<E>> {
    int nodeIds;

    Map<V, Node<V, E>> arena;

    Neighbors<V, E> neighbors;

    PriorityQueue<Node<V, E>> q = new PriorityQueue<>();

    public Dijkstra(Neighbors<V, E> neighbors) {
        this.arena = new java.util.HashMap<>();
        this.nodeIds = 0;
        this.neighbors = neighbors;
    }

    public Map<V, Node<V, E>> getArena() {
        return arena;
    }

    public void addVertex(V vertex, E gScore) {
        Node<V, E> n = new Node<>(vertex, gScore, nodeIds++);
        arena.put(vertex, n);
        q.offer(n);
    }

    public E findMin(Predicate<V> goal) {
        var a = findMinNode(goal);

        return a.getG();
    }

    private Node<V, E> findMinNode(Predicate<V> goal) {
        return arena.values().stream()
                .filter(n -> goal.test(n.vertex))
                .min(Comparator.naturalOrder()).orElseThrow();
    }

    public void calculate() {

        if (q.isEmpty()) {
            throw new AdventException("No vertices to process");
        }

        while (!q.isEmpty()) {
            var u = q.poll();
            if (u.getProcessed()) {
                continue;
            }

            u.setProcessed(true);
            neighbors.addNeighbors(u.vertex, (V v, E weight) -> {

                Node<V, E> n = new Node<>(v, u.gScore.combineEdge(weight), nodeIds++);
                n.parent = u;
                Node<V, E> existing = arena.computeIfAbsent(n.vertex, k -> n);

                if (existing.getG().compareTo(n.getG()) > 0) {
                    existing.setG(n.getG());
                    q.remove(existing);
                    q.add(existing);
                } else if (n == existing) {
                    q.add(existing);
                }
            });
        }

    }

    public List<V> getPath(Predicate<V> goal) {
        var n = findMinNode(goal);
        List<V> path = new ArrayList<>();
        while (n != null) {
            path.add(n.vertex);
            n = n.parent;
        }
        return path.reversed();
    }
}