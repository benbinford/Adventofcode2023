package com.benjaminbinford.utils;

class Node<V, E extends Weight<E>> {
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

    @Override
    public int hashCode() {
        return vertex.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node<?, ?> other = (Node<?, ?>) obj;
        return vertex.equals(other.vertex);
    }

    Node(V vertex, E gScore, int nodeId) {
        this.vertex = vertex;
        this.nodeId = nodeId;
        this.gScore = gScore;
    }

    static <V, E extends Weight<E>> int gComparison(Node<V, E> a, Node<V, E> b) {
        if (a.gScore == b.gScore) {
            return Integer.compare(a.nodeId, b.nodeId);
        }
        return a.gScore.compareTo(b.gScore);
    }
}