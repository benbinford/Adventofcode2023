package com.benjaminbinford.utils;

import java.util.Objects;

class Node<V, E extends Weight<E>> implements Comparable<Node<V, E>> {
    V vertex;
    E gScore;
    int nodeId;
    boolean processed = false;
    Node<V, E> parent;

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public boolean getProcessed() {
        return processed;
    }

    void setG(E g) {
        this.gScore = g;
    }

    E getG() {
        return gScore;
    }

    Node(V vertex, E gScore, int nodeId) {
        this.vertex = vertex;
        this.nodeId = nodeId;
        this.gScore = gScore;
    }

    @Override
    public int compareTo(Node<V, E> o) {
        if (this.gScore == o.gScore) {
            return Integer.compare(this.nodeId, o.nodeId);
        }
        return this.gScore.compareTo(o.gScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gScore, nodeId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Node<?, ?> other = (Node<?, ?>) obj;
        return Objects.equals(gScore, other.gScore) &&
                nodeId == other.nodeId;
    }

}